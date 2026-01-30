package models

case class Cart(
                 items: scala.collection.immutable.List[CartItem],
                 subtotal: BigDecimal,
                 tax: BigDecimal,
                 shipping: BigDecimal,
                 total: BigDecimal
               )