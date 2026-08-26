<?php

namespace App\Http\Controllers;

use App\Http\Requests\WithdrawalRequest;
use App\Http\Resources\WalletResource;
use App\Models\Wallet;
use App\Models\WalletTransaction;
use App\Models\WithdrawalRequest as WithdrawalRequestModel;
use App\Models\HelperProfile;
use Illuminate\Foundation\Auth\Access\AuthorizesRequests;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;

class WalletController extends Controller
{
    use AuthorizesRequests;

    /**
     * Get helper wallet details
     */
    public function show(Request $request)
    {
        $user = $request->user();

        if (!$user->isHelper()) {
            return response()->json([
                'message' => 'Only helpers can view wallet.',
            ], 403);
        }

        $helperProfile = $user->helperProfile;
        if (!$helperProfile) {
            return response()->json([
                'message' => 'Helper profile not found.',
            ], 404);
        }

        $wallet = Wallet::with('transactions')->where('helper_id', $helperProfile->id)->first();

        if (!$wallet) {
            // Create wallet if it doesn't exist
            $wallet = Wallet::create([
                'helper_id' => $helperProfile->id,
                'balance' => 0.00,
            ]);
        }

        return new WalletResource($wallet);
    }

    /**
     * Request withdrawal
     */
    public function withdraw(WithdrawalRequest $request)
    {
        $user = $request->user();

        if (!$user->isHelper()) {
            return response()->json([
                'message' => 'Only helpers can request withdrawals.',
            ], 403);
        }

        $helperProfile = $user->helperProfile;
        if (!$helperProfile) {
            return response()->json([
                'message' => 'Helper profile not found.',
            ], 404);
        }

        return DB::transaction(function () use ($request, $helperProfile) {
            // Lock wallet for update to prevent concurrent withdrawals
            $wallet = Wallet::where('helper_id', $helperProfile->id)
                ->lockForUpdate()
                ->first();

            if (!$wallet) {
                return response()->json([
                    'message' => 'Wallet not found.',
                ], 404);
            }

            // Verify sufficient balance
            if ($wallet->balance < $request->amount) {
                return response()->json([
                    'message' => 'Insufficient wallet balance.',
                ], 422);
            }

            // Debit wallet
            $wallet->decrement('balance', $request->amount);
            $wallet->touch();

            // Create wallet transaction (debit)
            $transaction = WalletTransaction::create([
                'wallet_id' => $wallet->id,
                'type' => 'debit',
                'amount' => $request->amount,
                'status' => 'completed',
            ]);

            // Create withdrawal request
            $withdrawalRequest = WithdrawalRequestModel::create([
                'helper_id' => $helperProfile->id,
                'amount' => $request->amount,
                'bank_account_details' => $request->bank_account_details,
                'status' => 'pending',
            ]);

            return response()->json([
                'message' => 'Withdrawal request submitted successfully.',
                'withdrawal_request_id' => $withdrawalRequest->id,
                'amount' => $withdrawalRequest->amount,
                'status' => $withdrawalRequest->status,
            ], 201);
        });
    }
}
