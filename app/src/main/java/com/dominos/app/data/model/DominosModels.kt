package com.dominos.app.data.model

import com.google.gson.annotations.SerializedName

data class StoreLocatorResponse(
    @SerializedName("Status") val status: Int = 0,
    @SerializedName("Stores") val stores: List<StoreInfo>? = null,
    @SerializedName("Address") val address: AddressInfo? = null
)

data class AddressInfo(
    @SerializedName("Street") val street: String? = null,
    @SerializedName("City") val city: String? = null,
    @SerializedName("Region") val region: String? = null,
    @SerializedName("PostalCode") val postalCode: String? = null
)

data class StoreInfo(
    @SerializedName("StoreID") val storeID: String? = null,
    @SerializedName("IsDeliveryStore") val isDeliveryStore: Boolean = false,
    @SerializedName("IsOnlineCapable") val isOnlineCapable: Boolean = false,
    @SerializedName("IsOpen") val isOpen: Boolean = false,
    @SerializedName("Phone") val phone: String? = null,
    @SerializedName("AddressDescription") val addressDescription: String? = null,
    @SerializedName("HoursDescription") val hoursDescription: String? = null,
    @SerializedName("ServiceHoursDescription") val serviceHoursDescription: ServiceHours? = null,
    @SerializedName("AllowDeliveryOrders") val allowDeliveryOrders: Boolean = false,
    @SerializedName("AllowCarryoutOrders") val allowCarryoutOrders: Boolean = false,
    @SerializedName("ServiceMethodEstimatedWaitMinutes") val estimatedWaitMinutes: EstimatedWaitMinutes? = null,
    @SerializedName("StoreCoordinates") val storeCoordinates: StoreCoordinates? = null,
    @SerializedName("ServiceIsOpen") val serviceIsOpen: ServiceIsOpen? = null,
    @SerializedName("ContactlessDelivery") val contactlessDelivery: String? = null
)

data class ServiceHours(
    @SerializedName("Delivery") val delivery: String? = null,
    @SerializedName("Carryout") val carryout: String? = null
)

data class EstimatedWaitMinutes(
    @SerializedName("Delivery") val delivery: WaitMinMax? = null,
    @SerializedName("Carryout") val carryout: WaitMinMax? = null
)

data class WaitMinMax(
    @SerializedName("Min") val min: Int = 0,
    @SerializedName("Max") val max: Int = 0
)

data class StoreCoordinates(
    @SerializedName("StoreLatitude") val latitude: String? = null,
    @SerializedName("StoreLongitude") val longitude: String? = null
)

data class ServiceIsOpen(
    @SerializedName("Delivery") val delivery: Boolean = false,
    @SerializedName("Carryout") val carryout: Boolean = false
)

data class MenuResponse(
    @SerializedName("Misc") val misc: MenuMisc? = null,
    @SerializedName("Categorization") val categorization: Categorization? = null,
    @SerializedName("Products") val products: Map<String, Product>? = null,
    @SerializedName("Variants") val variants: Map<String, Variant>? = null,
    @SerializedName("Sizes") val sizes: Map<String, Map<String, SizeDef>>? = null,
    @SerializedName("Toppings") val toppings: Map<String, Map<String, ToppingDef>>? = null,
    @SerializedName("Flavors") val flavors: Map<String, Map<String, FlavorDef>>? = null,
    @SerializedName("Sides") val sides: Map<String, Map<String, SideDef>>? = null
)

data class MenuMisc(
    @SerializedName("Status") val status: Int = 0,
    @SerializedName("StoreID") val storeID: String? = null,
    @SerializedName("BusinessDate") val businessDate: String? = null,
    @SerializedName("LanguageCode") val languageCode: String? = null
)

data class Categorization(
    @SerializedName("Food") val food: FoodCategory? = null
)

data class FoodCategory(
    @SerializedName("Categories") val categories: List<CategoryNode>? = null
)

data class CategoryNode(
    @SerializedName("Name") val name: String? = null,
    @SerializedName("Code") val code: String? = null,
    @SerializedName("Products") val products: List<String>? = null,
    @SerializedName("Categories") val categories: List<CategoryNode>? = null,
    @SerializedName("Tags") val tags: Map<String, String>? = null
)

data class Product(
    @SerializedName("Code") val code: String? = null,
    @SerializedName("Name") val name: String? = null,
    @SerializedName("Description") val description: String? = null,
    @SerializedName("ProductType") val productType: String? = null,
    @SerializedName("ImageCode") val imageCode: String? = null,
    @SerializedName("Tags") val tags: Map<String, Any>? = null,
    @SerializedName("AvailableToppings") val availableToppings: String? = null,
    @SerializedName("DefaultToppings") val defaultToppings: String? = null
)

data class Variant(
    @SerializedName("Code") val code: String? = null,
    @SerializedName("Name") val name: String? = null,
    @SerializedName("Price") val price: String? = null,
    @SerializedName("ProductCode") val productCode: String? = null,
    @SerializedName("SizeCode") val sizeCode: String? = null,
    @SerializedName("FlavorCode") val flavorCode: String? = null,
    @SerializedName("ImageCode") val imageCode: String? = null
)

data class SizeDef(
    @SerializedName("Code") val code: String? = null,
    @SerializedName("Name") val name: String? = null,
    @SerializedName("SortSeq") val sortSeq: String? = null
)

data class ToppingDef(
    @SerializedName("Code") val code: String? = null,
    @SerializedName("Name") val name: String? = null,
    @SerializedName("Price") val price: String? = null,
    @SerializedName("Tags") val tags: Map<String, Any>? = null
)

data class FlavorDef(
    @SerializedName("Code") val code: String? = null,
    @SerializedName("Name") val name: String? = null,
    @SerializedName("Price") val price: String? = null
)

data class SideDef(
    @SerializedName("Code") val code: String? = null,
    @SerializedName("Name") val name: String? = null,
    @SerializedName("Price") val price: String? = null
)

data class OrderPayload(
    @SerializedName("Address") val address: OrderAddress? = null,
    @SerializedName("Coupons") val coupons: List<String>? = null,
    @SerializedName("Email") val email: String? = null,
    @SerializedName("FirstName") val firstName: String? = null,
    @SerializedName("LastName") val lastName: String? = null,
    @SerializedName("LanguageCode") val languageCode: String = "en",
    @SerializedName("OrderChannel") val orderChannel: String = "OLO",
    @SerializedName("OrderMethod") val orderMethod: String = "Web",
    @SerializedName("OrderTaker") val orderTaker: String? = null,
    @SerializedName("Payments") val payments: List<PaymentPayload>? = null,
    @SerializedName("Phone") val phone: String? = null,
    @SerializedName("Products") val products: List<OrderProduct>? = null,
    @SerializedName("ServiceMethod") val serviceMethod: String = "Delivery",
    @SerializedName("SourceOrganizationURI") val sourceOrganizationURI: String = "order.dominos.com",
    @SerializedName("StoreID") val storeID: String? = null,
    @SerializedName("NoCombine") val noCombine: Boolean = true,
    @SerializedName("Version") val version: String = "1.0",
    @SerializedName("MetaData") val metaData: Map<String, Any>? = null
)

data class OrderAddress(
    @SerializedName("Street") val street: String? = null,
    @SerializedName("StreetNumber") val streetNumber: String? = null,
    @SerializedName("StreetName") val streetName: String? = null,
    @SerializedName("UnitType") val unitType: String? = null,
    @SerializedName("UnitNumber") val unitNumber: String? = null,
    @SerializedName("City") val city: String? = null,
    @SerializedName("Region") val region: String? = null,
    @SerializedName("PostalCode") val postalCode: String? = null,
    @SerializedName("DeliveryInstructions") val deliveryInstructions: String? = null,
    @SerializedName("Type") val type: String = "House"
)

data class OrderProduct(
    @SerializedName("Code") val code: String? = null,
    @SerializedName("Qty") val qty: Int = 1,
    @SerializedName("Options") val options: Map<String, Map<String, String>>? = null,
    @SerializedName("ID") val id: Int = 1,
    @SerializedName("isNew") val isNew: Boolean = true
)

data class PaymentPayload(
    @SerializedName("Type") val type: String? = "Cash",
    @SerializedName("Amount") val amount: String? = null,
    @SerializedName("Number") val number: String? = null,
    @SerializedName("CardType") val cardType: String? = null,
    @SerializedName("Expiration") val expiration: String? = null,
    @SerializedName("SecurityCode") val securityCode: String? = null,
    @SerializedName("PostalCode") val postalCode: String? = null,
    @SerializedName("TipAmount") val tipAmount: String? = null
)

data class OrderResponse(
    @SerializedName("Status") val status: Int = 0,
    @SerializedName("StatusItems") val statusItems: List<StatusItem>? = null,
    @SerializedName("Order") val order: OrderResult? = null,
    @SerializedName("AmountsBreakdown") val amountsBreakdown: AmountsBreakdown? = null,
    @SerializedName("EstimatedWaitMinutes") val estimatedWaitMinutes: String? = null,
    @SerializedName("OrderID") val orderID: String? = null,
    @SerializedName("PulseOrderGuid") val pulseOrderGuid: String? = null,
    @SerializedName("BusinessDate") val businessDate: String? = null
)

data class StatusItem(
    @SerializedName("Code") val code: String? = null,
    @SerializedName("Message") val message: String? = null
)

data class OrderResult(
    @SerializedName("OrderID") val orderID: String? = null,
    @SerializedName("EstimatedWaitMinutes") val estimatedWaitMinutes: String? = null,
    @SerializedName("AmountsBreakdown") val amountsBreakdown: AmountsBreakdown? = null,
    @SerializedName("Products") val products: List<OrderProduct>? = null,
    @SerializedName("Status") val status: Int = 0,
    @SerializedName("PulseOrderGuid") val pulseOrderGuid: String? = null
)

data class AmountsBreakdown(
    @SerializedName("FoodAndBeverage") val foodAndBeverage: String? = null,
    @SerializedName("Adjustment") val adjustment: String? = null,
    @SerializedName("Surcharge") val surcharge: String? = null,
    @SerializedName("DeliveryFee") val deliveryFee: String? = null,
    @SerializedName("Tax") val tax: Double = 0.0,
    @SerializedName("Customer") val customer: String? = null,
    @SerializedName("Savings") val savings: String? = null,
    @SerializedName("Tip") val tip: String? = null
)

data class TrackingResponse(
    @SerializedName("OrderID") val orderID: String? = null,
    @SerializedName("Status") val status: String? = null,
    @SerializedName("StatusItems") val statusItems: List<TrackingStatusItem>? = null,
    @SerializedName("PulseOrderGuid") val pulseOrderGuid: String? = null,
    @SerializedName("StoreID") val storeID: String? = null,
    @SerializedName("OrderStage") val orderStage: String? = null,
    @SerializedName("Amounts") val amounts: Amounts? = null
)

data class TrackingStatusItem(
    @SerializedName("Code") val code: String? = null,
    @SerializedName("Message") val message: String? = null,
    @SerializedName("DateTime") val dateTime: String? = null
)

data class Amounts(
    @SerializedName("FoodAndBeverage") val foodAndBeverage: String? = null,
    @SerializedName("DeliveryFee") val deliveryFee: String? = null,
    @SerializedName("Tax") val tax: Double = 0.0,
    @SerializedName("Customer") val customer: String? = null
)

data class CartItem(
    val productCode: String,
    val productName: String,
    val quantity: Int = 1,
    val price: String? = null,
    val options: Map<String, Map<String, String>>? = null,
    val sizeCode: String? = null,
    val flavorCode: String? = null,
    val variantCode: String? = null,
    val id: Int
)

data class SavedCustomer(
    val firstName: String = "",
    val lastName: String = "",
    val phone: String = "",
    val email: String = "",
    val street: String = "",
    val city: String = "",
    val region: String = "",
    val postalCode: String = "",
    val isLoggedIn: Boolean = false
)
