package com.dominos.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dominos.app.data.model.*
import com.dominos.app.data.repository.DominosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class MenuUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val storeId: String? = null,
    val menuResponse: MenuResponse? = null,
    val categories: List<CategoryNode> = emptyList(),
    val selectedCategoryIndex: Int = 0,
    val flatCategories: List<CategoryNode> = emptyList(),
    val selectedCategoryProducts: List<MenuDisplayItem> = emptyList()
)

data class MenuDisplayItem(
    val productCode: String,
    val productName: String,
    val description: String?,
    val productType: String?,
    val imageCode: String?,
    val price: String?,
    val variants: List<Variant> = emptyList(),
    val sizes: List<SizeDef> = emptyList(),
    val flavors: List<FlavorDef> = emptyList(),
    val availableToppings: List<ToppingDef> = emptyList()
)

class MenuViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = DominosRepository()
    private val _uiState = MutableStateFlow(MenuUiState())
    val uiState: StateFlow<MenuUiState> = _uiState

    fun loadMenu(storeId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, storeId = storeId)
            val result = repository.getMenu(storeId)
            result.fold(
                onSuccess = { menu ->
                    val cats = flattenCategories(menu)
                    val products = getCategoryProducts(menu, cats.firstOrNull(), _uiState.value.selectedCategoryIndex)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        menuResponse = menu,
                        categories = menu.categorization?.food?.categories ?: emptyList(),
                        flatCategories = cats,
                        selectedCategoryProducts = products
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load menu"
                    )
                }
            )
        }
    }

    fun selectCategory(index: Int) {
        val state = _uiState.value
        val cats = state.flatCategories
        if (index < 0 || index >= cats.size) return
        val selected = cats[index]
        val products = getCategoryProducts(state.menuResponse, selected, index)
        _uiState.value = state.copy(
            selectedCategoryIndex = index,
            selectedCategoryProducts = products
        )
    }

    fun getProductVariants(productCode: String): List<Variant> {
        val menu = _uiState.value.menuResponse ?: return emptyList()
        return menu.variants?.values?.filter { it.productCode == productCode } ?: emptyList()
    }

    fun getProductSizes(productCode: String): List<SizeDef> {
        val menu = _uiState.value.menuResponse ?: return emptyList()
        val variants = getProductVariants(productCode)
        val sizeCodes = variants.mapNotNull { it.sizeCode }.distinct()
        val allSizes = menu.sizes?.values?.flatMap { it.values } ?: emptyList()
        return allSizes.filter { it.code in sizeCodes }
    }

    fun getProductFlavors(productCode: String, categoryCode: String?): List<FlavorDef> {
        val menu = _uiState.value.menuResponse ?: return emptyList()
        if (categoryCode == null) return emptyList()
        val flavorMap = menu.flavors?.get(categoryCode) ?: return emptyList()
        return flavorMap.values.toList()
    }

    fun getAvailableToppings(productCode: String): List<ToppingDef> {
        val menu = _uiState.value.menuResponse ?: return emptyList()
        val product = menu.products?.get(productCode) ?: return emptyList()
        val avail = product.availableToppings ?: return emptyList()
        val toppingCodes = avail.split(",").map { it.trim() }
        val allToppings = menu.toppings?.values?.flatMap { it.values } ?: emptyList()
        return allToppings.filter { it.code in toppingCodes }
    }

    private fun flattenCategories(menu: MenuResponse): List<CategoryNode> {
        val result = mutableListOf<CategoryNode>()
        fun dfs(nodes: List<CategoryNode>?) {
            if (nodes == null) return
            for (node in nodes) {
                result.add(node)
                if (!node.categories.isNullOrEmpty()) {
                    dfs(node.categories)
                }
            }
        }
        dfs(menu.categorization?.food?.categories)
        return result
    }

    private fun getCategoryProducts(menu: MenuResponse?, category: CategoryNode?, index: Int): List<MenuDisplayItem> {
        if (menu == null || category == null) return emptyList()

        val productCodes = getAllProductCodes(category)
        val products = menu.products ?: return emptyList()
        val variants = menu.variants ?: return emptyList()
        val allToppings = menu.toppings?.values?.flatMap { it.values } ?: emptyList()

        return productCodes.mapNotNull { code ->
            val product = products[code] ?: return@mapNotNull null
            val productVariants = variants.values.filter { it.productCode == code }
            val price = productVariants.minOfOrNull {
                it.price?.toDoubleOrNull() ?: Double.MAX_VALUE
            }?.let { "$${String.format("%.2f", it)}" }
            val availCodes = product.availableToppings?.split(",")?.map { it.trim() } ?: emptyList()
            MenuDisplayItem(
                productCode = code,
                productName = product.name ?: code,
                description = product.description,
                productType = product.productType,
                imageCode = product.imageCode ?: code,
                price = price,
                variants = productVariants,
                sizes = getProductSizes(code),
                flavors = emptyList(),
                availableToppings = allToppings.filter { it.code in availCodes }
            )
        }
    }

    private fun getAllProductCodes(category: CategoryNode): List<String> {
        val codes = mutableListOf<String>()
        category.products?.let { codes.addAll(it) }
        category.categories?.forEach { codes.addAll(getAllProductCodes(it)) }
        return codes
    }
}
