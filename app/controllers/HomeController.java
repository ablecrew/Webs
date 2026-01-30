
package controllers;


import com.google.inject.Inject;
import models.Address;
import models.Cart;
import models.CartItem;
import play.api.i18n.Messages;
import play.api.i18n.MessagesApi;
import play.mvc.*;
import scala.collection.JavaConverters;
import scala.math.BigDecimal$;
import views.html.*;

import java.util.ArrayList;


import static play.shaded.ahc.org.asynchttpclient.Dsl.request;

public class HomeController extends Controller {


    public Result index() {
        return ok(index.render()); }

    public Result signup() {
        return ok(signup.render()); }

    public Result login() {
        return ok(login.render()); }

    public Result logout() {
        return redirect(routes.HomeController.index()).withNewSession();
    }

    public Result dashboard() {

        return ok(dashboard.render()); }

    public Result services() {
        return ok(services.render()); }

    public Result aboutus() {
        return ok(aboutus.render()); }

    public Result ourteam() {
        return ok(ourteam.render()); }

    private final MessagesApi messagesApi;

    @Inject
    public HomeController(MessagesApi messagesApi) {
        this.messagesApi = messagesApi;
    }
    public Result checkout(Http.Request req) {
        // Java list of CartItem
        // 1. Create Java list of CartItems
        java.util.List<CartItem> javaItems = new ArrayList<>();
        javaItems.add(new CartItem(
                "1",                        // id
                "Cool Sneakers",            // name
                "Red / Size 9",             // variant
                "/assets/images/sneakers.jpg", // imageUrl
                BigDecimal$.MODULE$.apply(79.99),    // price
                BigDecimal$.MODULE$.apply(99.99),    // originalPrice
                2,                           // quantity
                true,                        // inStock
                true,                        // freeShipping
                BigDecimal$.MODULE$.apply(20.0)       // discount
        ));

        javaItems.add(new CartItem(
                "2",
                "Casual T-Shirt",
                "Blue / Medium",
                "/assets/images/tshirt.jpg",
                BigDecimal$.MODULE$.apply(29.99),
                BigDecimal$.MODULE$.apply(39.99),
                1,
                true,
                false,
                BigDecimal$.MODULE$.apply(10.0)
        ));

        // Convert to Scala immutable List
        scala.collection.immutable.List<CartItem> scalaItems = JavaConverters.asScalaBuffer(javaItems).toList();


        // 3. Create Cart object
        Cart cart = new Cart(
                scalaItems,
                BigDecimal$.MODULE$.apply(159.97),   // subtotal
                BigDecimal$.MODULE$.apply(15.99),    // tax
                BigDecimal$.MODULE$.apply(5.00),     // shipping
                BigDecimal$.MODULE$.apply(180.96)    // total
        );


        // 4. Create sample addresses
        java.util.List<Address> javaAddresses = new ArrayList<>();
        javaAddresses.add(new Address(
                1L,
                "Home",               // label
                "John Doe",           // fullName
                "123 Main St",        // street
                "Nairobi",            // city
                "Nairobi County",     // state
                "00100",              // zip
                true                  // isDefault
        ));

        javaAddresses.add(new Address(
                2L,
                "Work",
                "John Doe",
                "456 Office Rd",
                "Nairobi",
                "Nairobi County",
                "00200",
                false
        ));

        // Convert to Scala immutable List
        scala.collection.immutable.List<Address> scalaAddresses = JavaConverters.asScalaBuffer(javaAddresses).toList();

        // 3. Get Messages for template
        Messages msgs = messagesApi.preferred(req);

        // 4. Render template
        return ok(views.html.checkout.render(cart, scalaAddresses, req, msgs));
    }


    public Result forgotpassword() {
        return ok(forgotpassword.render()); }

    public Result terms() {
        return ok(terms.render()); }

    public Result policy() {
        return ok(policy.render()); }

    public Result contact() {
        return ok(contact.render()); }

    public Result auth() {
        return ok(auth.render()); }

    public Result products() {
        return ok(products.render()); }

    public Result userprofile() {
        return ok(userprofile.render()); }

    public Result product() {
        return ok(product.render()); }

    public Result cart() {
        return ok(cart.render()); }
}
