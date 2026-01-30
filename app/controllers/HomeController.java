
package controllers;


import play.mvc.*;
import views.html.*;

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

    public Result portfolio() {
        return ok(portfolio.render()); }

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

    public Result customerdashboard() {
        return ok(customerdashboard.render()); }

    public Result cmessages() {
        return ok(cmessages.render()); }

    public Result cinvoices() {
        return ok(cinvoices.render()); }

    public Result cfiles() {
        return ok(cfiles.render()); }

    public Result csettings() {
        return ok(csettings.render()); }
}
