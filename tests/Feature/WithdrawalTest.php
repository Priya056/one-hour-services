<?php

namespace Tests\Feature;

use App\Models\User;
use App\Models\HelperProfile;
use App\Models\Wallet;
use App\Models\WalletTransaction;
use App\Models\WithdrawalRequest;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;

class WithdrawalTest extends TestCase
{
    use RefreshDatabase;

    /**
     * Helper can request withdrawal.
     */
    public function test_helper_can_request_withdrawal()
    {
        $helper = User::factory()->create(['role' => 'helper']);
        $helperProfile = HelperProfile::factory()->create(['user_id' => $helper->id]);
        $wallet = Wallet::create([
            'helper_id' => $helperProfile->id,
            'balance' => 1000.00,
        ]);

        $token = $helper->createToken('auth-token')->plainTextToken;

        $response = $this->withToken($token)
            ->postJson('/api/wallet/withdraw', [
                'amount' => 500.00,
                'bank_account_details' => ['account_number' => '1234567890'],
            ]);

        $response->assertStatus(201)
            ->assertJsonPath('status', 'pending');

        // Check wallet was debited
        $wallet->refresh();
        $this->assertEquals(500.00, $wallet->balance);

        // Check withdrawal request was created
        $withdrawal = WithdrawalRequest::where('helper_id', $helperProfile->id)->first();
        $this->assertNotNull($withdrawal);
        $this->assertEquals(500.00, $withdrawal->amount);
    }

    /**
     * Withdrawal amount cannot exceed balance.
     */
    public function test_withdrawal_amount_cannot_exceed_balance()
    {
        $helper = User::factory()->create(['role' => 'helper']);
        $helperProfile = HelperProfile::factory()->create(['user_id' => $helper->id]);
        $wallet = Wallet::create([
            'helper_id' => $helperProfile->id,
            'balance' => 500.00,
        ]);

        $token = $helper->createToken('auth-token')->plainTextToken;

        $response = $this->withToken($token)
            ->postJson('/api/wallet/withdraw', [
                'amount' => 1000.00,
                'bank_account_details' => ['account_number' => '1234567890'],
            ]);

        $response->assertStatus(422)
            ->assertJsonPath('message', 'Insufficient wallet balance.');
    }

    /**
     * Wallet is debited immediately on withdrawal request.
     */
    public function test_wallet_debited_immediately_on_withdrawal_request()
    {
        $helper = User::factory()->create(['role' => 'helper']);
        $helperProfile = HelperProfile::factory()->create(['user_id' => $helper->id]);
        $wallet = Wallet::create([
            'helper_id' => $helperProfile->id,
            'balance' => 1000.00,
        ]);

        $token = $helper->createToken('auth-token')->plainTextToken;

        $this->withToken($token)
            ->postJson('/api/wallet/withdraw', [
                'amount' => 500.00,
                'bank_account_details' => ['account_number' => '1234567890'],
            ]);

        $wallet->refresh();
        $this->assertEquals(500.00, $wallet->balance);
    }

    /**
     * Admin can process pending withdrawal.
     */
    public function test_admin_can_process_pending_withdrawal()
    {
        $helper = User::factory()->create(['role' => 'helper']);
        $helperProfile = HelperProfile::factory()->create(['user_id' => $helper->id]);
        $admin = User::factory()->create(['role' => 'admin']);
        $wallet = Wallet::create([
            'helper_id' => $helperProfile->id,
            'balance' => 1000.00,
        ]);

        $withdrawal = WithdrawalRequest::create([
            'helper_id' => $helperProfile->id,
            'amount' => 500.00,
            'bank_account_details' => ['account_number' => '1234567890'],
            'status' => 'pending',
        ]);

        $adminToken = $admin->createToken('auth-token')->plainTextToken;

        $response = $this->withToken($adminToken)
            ->patchJson("/api/admin/withdrawals/{$withdrawal->id}/process");

        $response->assertStatus(200)
            ->assertJsonPath('message', 'Withdrawal processed successfully.')
            ->assertJsonPath('withdrawal.status', 'processed');

        $withdrawal->refresh();
        $this->assertNotNull($withdrawal->processed_at);
    }

    /**
     * Admin rejection reverses the debit.
     */
    public function test_admin_rejection_reverses_debit()
    {
        $helper = User::factory()->create(['role' => 'helper']);
        $helperProfile = HelperProfile::factory()->create(['user_id' => $helper->id]);
        $admin = User::factory()->create(['role' => 'admin']);
        $wallet = Wallet::create([
            'helper_id' => $helperProfile->id,
            'balance' => 1000.00,
        ]);

        $withdrawal = WithdrawalRequest::create([
            'helper_id' => $helperProfile->id,
            'amount' => 500.00,
            'bank_account_details' => ['account_number' => '1234567890'],
            'status' => 'pending',
        ]);

        $adminToken = $admin->createToken('auth-token')->plainTextToken;

        $response = $this->withToken($adminToken)
            ->patchJson("/api/admin/withdrawals/{$withdrawal->id}/reject");

        $response->assertStatus(200)
            ->assertJsonPath('message', 'Withdrawal rejected and funds returned to wallet.');

        // Wallet should be credited back
        $wallet->refresh();
        $this->assertEquals(1500.00, $wallet->balance);

        // Should have a credit transaction
        $creditTransaction = WalletTransaction::where('wallet_id', $wallet->id)
            ->where('type', 'credit')
            ->where('amount', 500.00)
            ->first();
        $this->assertNotNull($creditTransaction);
    }

    /**
     * Non-admin cannot process withdrawals.
     */
    public function test_non_admin_cannot_process_withdrawals()
    {
        $helper = User::factory()->create(['role' => 'helper']);
        $helperProfile = HelperProfile::factory()->create(['user_id' => $helper->id]);
        $withdrawal = WithdrawalRequest::create([
            'helper_id' => $helperProfile->id,
            'amount' => 500.00,
            'bank_account_details' => ['account_number' => '1234567890'],
            'status' => 'pending',
        ]);

        $helperToken = $helper->createToken('auth-token')->plainTextToken;

        $response = $this->withToken($helperToken)
            ->patchJson("/api/admin/withdrawals/{$withdrawal->id}/process");

        $response->assertStatus(403);
    }
}
