<?php

namespace App\Http\Controllers;

use App\Models\WithdrawalRequest;
use App\Models\Wallet;
use App\Models\WalletTransaction;
use Illuminate\Foundation\Auth\Access\AuthorizesRequests;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;

class AdminWithdrawalController extends Controller
{
    use AuthorizesRequests;

    /**
     * Get all withdrawal requests
     */
    public function index(Request $request)
    {
        if (!$request->user()->isAdmin()) {
            return response()->json(['message' => 'Unauthorized'], 403);
        }

        $withdrawals = WithdrawalRequest::with(['helper', 'helper.user'])
            ->orderBy('requested_at', 'desc')
            ->get();

        return response()->json($withdrawals);
    }

    /**
     * Process withdrawal request
     */
    public function process(Request $request, $id)
    {
        if (!$request->user()->isAdmin()) {
            return response()->json(['message' => 'Unauthorized'], 403);
        }

        $withdrawal = WithdrawalRequest::findOrFail($id);

        if ($withdrawal->status !== 'pending') {
            return response()->json([
                'message' => 'Withdrawal request is not pending.',
            ], 422);
        }

        $withdrawal->update([
            'status' => 'processed',
            'processed_at' => now(),
        ]);

        return response()->json([
            'message' => 'Withdrawal processed successfully.',
            'withdrawal' => $withdrawal,
        ]);
    }

    /**
     * Reject withdrawal request
     */
    public function reject(Request $request, $id)
    {
        if (!$request->user()->isAdmin()) {
            return response()->json(['message' => 'Unauthorized'], 403);
        }

        return DB::transaction(function () use ($request, $id) {
            $withdrawal = WithdrawalRequest::findOrFail($id);

            if ($withdrawal->status !== 'pending') {
                return response()->json([
                    'message' => 'Withdrawal request is not pending.',
                ], 422);
            }

            // Reverse the debit by crediting the wallet
            $wallet = Wallet::where('helper_id', $withdrawal->helper_id)->first();
            if ($wallet) {
                $wallet->increment('balance', $withdrawal->amount);
                $wallet->touch();

                // Create credit transaction to reverse the debit
                WalletTransaction::create([
                    'wallet_id' => $wallet->id,
                    'type' => 'credit',
                    'amount' => $withdrawal->amount,
                    'status' => 'completed',
                ]);
            }

            $withdrawal->update([
                'status' => 'rejected',
                'processed_at' => now(),
            ]);

            return response()->json([
                'message' => 'Withdrawal rejected and funds returned to wallet.',
                'withdrawal' => $withdrawal,
            ]);
        });
    }
}
