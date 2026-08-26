<?php

namespace Tests\Feature\MySQL;

use App\Models\User;
use App\Models\HelperProfile;
use App\Models\KYCDocument;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;

class KYCTest extends TestCase
{
    use RefreshDatabase;

    /**
     * Test helper can submit KYC document.
     */
    public function test_helper_can_submit_kyc_document()
    {
        $helper = User::factory()->create(['role' => 'helper']);
        $helperProfile = HelperProfile::factory()->create(['user_id' => $helper->id]);
        $token = $helper->createToken('auth-token')->plainTextToken;

        $response = $this->withToken($token)
            ->postJson('/api/kyc', [
                'document_type' => 'Aadhaar',
                'document_url' => 'https://example.com/document.pdf',
            ]);

        $response->assertStatus(201)
            ->assertJsonPath('data.document_type', 'Aadhaar')
            ->assertJsonPath('data.status', 'pending');

        $this->assertDatabaseHas('kyc_documents', [
            'helper_id' => $helperProfile->id,
            'document_type' => 'Aadhaar',
        ]);
    }

    /**
     * Test helper can view their KYC status.
     */
    public function test_helper_can_view_their_kyc_status()
    {
        $helper = User::factory()->create(['role' => 'helper']);
        $helperProfile = HelperProfile::factory()->create(['user_id' => $helper->id]);
        KYCDocument::factory()->create(['helper_id' => $helperProfile->id]);
        $token = $helper->createToken('auth-token')->plainTextToken;

        $response = $this->withToken($token)
            ->getJson('/api/kyc');

        $response->assertStatus(200);
    }

    /**
     * Test admin can approve KYC document.
     */
    public function test_admin_can_approve_kyc_document()
    {
        $admin = User::factory()->create(['role' => 'admin']);
        $helper = User::factory()->create(['role' => 'helper']);
        $helperProfile = HelperProfile::factory()->create(['user_id' => $helper->id]);
        $kycDocument = KYCDocument::factory()->create(['helper_id' => $helperProfile->id]);
        $token = $admin->createToken('auth-token')->plainTextToken;

        $response = $this->withToken($token)
            ->patchJson("/api/admin/kyc/{$kycDocument->id}/approve");

        $response->assertStatus(200)
            ->assertJsonPath('data.status', 'approved');
    }

    /**
     * Test admin can reject KYC document.
     */
    public function test_admin_can_reject_kyc_document()
    {
        $admin = User::factory()->create(['role' => 'admin']);
        $helper = User::factory()->create(['role' => 'helper']);
        $helperProfile = HelperProfile::factory()->create(['user_id' => $helper->id]);
        $kycDocument = KYCDocument::factory()->create(['helper_id' => $helperProfile->id]);
        $token = $admin->createToken('auth-token')->plainTextToken;

        $response = $this->withToken($token)
            ->patchJson("/api/admin/kyc/{$kycDocument->id}/reject");

        $response->assertStatus(200)
            ->assertJsonPath('data.status', 'rejected');
    }

    /**
     * Test non-admin cannot approve KYC document.
     */
    public function test_non_admin_cannot_approve_kyc_document()
    {
        $helper = User::factory()->create(['role' => 'helper']);
        $helperProfile = HelperProfile::factory()->create(['user_id' => $helper->id]);
        $kycDocument = KYCDocument::factory()->create(['helper_id' => $helperProfile->id]);
        $token = $helper->createToken('auth-token')->plainTextToken;

        $response = $this->withToken($token)
            ->patchJson("/api/admin/kyc/{$kycDocument->id}/approve");

        $response->assertStatus(403);
    }
}
