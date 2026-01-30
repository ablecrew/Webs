package models

case class CartItem(
                     id: String,
                     name: String,
                     variant: String,
                     imageUrl: String,
                     price: BigDecimal,
                     originalPrice: BigDecimal,
                     quantity: Int,
                     inStock: Boolean,
                     freeShipping: Boolean,
                     discount: BigDecimal
                   )
