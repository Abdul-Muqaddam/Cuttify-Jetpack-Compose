package com.example.videotoaudioconverter.presentation.Premium_Screen

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BillingHelper(private val context: Context) {

    private val _isPremium = MutableStateFlow(false)
    val isPremium: StateFlow<Boolean> = _isPremium

    private var billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                for (purchase in purchases) {
                    handlePurchase(purchase)
                }
            }
        }
        .enablePendingPurchases()
        .build()

    // ── Connect to Google Play ─────────────────────────────────────
    fun startConnection(onReady: () -> Unit = {}) {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    onReady()
                    checkExistingPurchases()
                }
            }
            override fun onBillingServiceDisconnected() {
                // Retry connection if needed
            }
        })
    }

    // ── Launch Purchase Flow ───────────────────────────────────────
    fun launchPurchase(activity: Activity, productId: String, isSubscription: Boolean) {
        val productType = if (isSubscription) BillingClient.ProductType.SUBS
        else BillingClient.ProductType.INAPP

        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(productId)
                .setProductType(productType)
                .build()
        )

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        billingClient.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK
                && productDetailsList.isNotEmpty()
            ) {
                val productDetails = productDetailsList[0]

                val productDetailsParamsList = if (isSubscription) {
                    val offerToken = productDetails
                        .subscriptionOfferDetails?.get(0)?.offerToken ?: return@queryProductDetailsAsync
                    listOf(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                            .setProductDetails(productDetails)
                            .setOfferToken(offerToken)
                            .build()
                    )
                } else {
                    listOf(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                            .setProductDetails(productDetails)
                            .build()
                    )
                }

                val billingFlowParams = BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(productDetailsParamsList)
                    .build()

                billingClient.launchBillingFlow(activity, billingFlowParams)
            }
        }
    }

    // ── Handle Purchase ────────────────────────────────────────────
    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            _isPremium.value = true

            // Acknowledge the purchase
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
                billingClient.acknowledgePurchase(acknowledgePurchaseParams) { }
            }
        }
    }

    // ── Check Existing Purchases ───────────────────────────────────
    private fun checkExistingPurchases() {
        // Check one time purchases
        billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        ) { _, purchases ->
            purchases.forEach { handlePurchase(it) }
        }

        // Check subscriptions
        billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        ) { _, purchases ->
            purchases.forEach { handlePurchase(it) }
        }
    }

    fun endConnection() {
        billingClient.endConnection()
    }
}