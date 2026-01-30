package models

case class Address(
                    id: Long,           // unique identifier for the address
                    label: String,      // e.g., "Home", "Work"
                    fullName: String,   // recipient full name
                    street: String,     // street address
                    city: String,       // city
                    state: String,      // state/province
                    zip: String,        // postal code
                    isDefault: Boolean  // whether this is the default address
                  )
