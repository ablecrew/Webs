package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import play.libs.ws.*;
import play.mvc.*;
import views.html.*;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;

public class HomeController extends Controller {

    private final WSClient ws;
    private final ObjectMapper mapper = new ObjectMapper();

    @Inject
    public HomeController(WSClient ws) {
        this.ws = ws;
    }

    // --- API Consumption: Dashboard ---
    public CompletionStage<Result> index() {
        // Dashboard KPI API endpoints
        String totalPropertiesUrl = "http://localhost/rentpulse-api/dashboard/get_properties.php";
        String totalTenantsUrl = "http://localhost/rentpulse-api/dashboard/total_tenants.php";
        String totalLandlordsUrl = "http://localhost/rentpulse-api/dashboard/total_landlords.php";
        String pendingPaymentsUrl = "http://localhost/rentpulse-api/dashboard/pending_payments.php";
        String totalCollectedUrl = "http://localhost/rentpulse-api/dashboard/monthly_collection.php";
        String recentActivityUrl = "http://localhost/rentpulse-api/dashboard/get_activities.php";

        // Helper to safely parse JSON
        java.util.function.Function<String, JsonNode> parseJson = (String body) -> {
            try {
                return mapper.readTree(body);
            } catch (Exception e) {
                e.printStackTrace();
                return mapper.createObjectNode();
            }
        };

        // Fetch all APIs asynchronously
        CompletionStage<JsonNode> propertiesF = ws.url(totalPropertiesUrl)
                .addHeader("X-App-Username", "rentpulse_app_001")
                .addHeader("X-App-Password", "b99marasighan/X")
                .get().thenApply(resp -> parseJson.apply(resp.getBody()));

        CompletionStage<JsonNode> tenantsF = ws.url(totalTenantsUrl)
                .addHeader("X-App-Username", "rentpulse_app_001")
                .addHeader("X-App-Password", "b99marasighan/X")
                .get().thenApply(resp -> parseJson.apply(resp.getBody()));

        CompletionStage<JsonNode> landlordsF = ws.url(totalLandlordsUrl)
                .addHeader("X-App-Username", "rentpulse_app_001")
                .addHeader("X-App-Password", "b99marasighan/X")
                .get().thenApply(resp -> parseJson.apply(resp.getBody()));

        CompletionStage<JsonNode> pendingF = ws.url(pendingPaymentsUrl)
                .addHeader("X-App-Username", "rentpulse_app_001")
                .addHeader("X-App-Password", "b99marasighan/X")
                .get().thenApply(resp -> parseJson.apply(resp.getBody()));

        CompletionStage<JsonNode> collectedF = ws.url(totalCollectedUrl)
                .addHeader("X-App-Username", "rentpulse_app_001")
                .addHeader("X-App-Password", "b99marasighan/X")
                .get().thenApply(resp -> parseJson.apply(resp.getBody()));

        CompletionStage<JsonNode> activityF = ws.url(recentActivityUrl)
                .addHeader("X-App-Username", "rentpulse_app_001")
                .addHeader("X-App-Password", "b99marasighan/X")
                .get().thenApply(resp -> parseJson.apply(resp.getBody()));

        // Combine all responses safely into a mutable list
        return landlordsF.thenCombine(tenantsF, (landlordsJson, tenantsJson) -> {
                    List<JsonNode> list = new ArrayList<>();
                    list.add(landlordsJson);
                    list.add(tenantsJson);
                    return list;
                })
                .thenCombine(propertiesF, (list12, propertiesJson) -> { list12.add(propertiesJson); return list12; })
                .thenCombine(pendingF, (list123, pendingJson) -> { list123.add(pendingJson); return list123; })
                .thenCombine(collectedF, (list1234, collectedJson) -> { list1234.add(collectedJson); return list1234; })
                .thenCombine(activityF, (allList, activityJson) -> { allList.add(activityJson); return allList; })
                .thenApply(all -> {
                    List<Map<String, Object>> dashboardList = new ArrayList<>();
                    try {
                        Map<String, Object> kpis = Map.of(
                                "totalLandlords", all.get(0).path("totalLandlords").asInt(0),
                                "totalTenants", all.get(1).path("totalTenants").asInt(0),
                                "totalProperties", all.get(2).path("totalProperties").asInt(0),
                                "pendingPayments", all.get(3).path("pendingPayments").asInt(0),
                                "monthlyCollection", all.get(4).path("monthlyCollection").asDouble(0.0)
                        );

                        List<Map<String, Object>> activities = new ArrayList<>();
                        JsonNode activityData = all.get(5).path("data");
                        if (activityData.isArray()) {
                            for (JsonNode node : activityData) {
                                activities.add(mapper.convertValue(node, Map.class));
                            }
                        }

                        Map<String, Object> combined = Map.of(
                                "kpis", kpis,
                                "activities", activities
                        );

                        dashboardList.add(combined);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return ok(views.html.index.render(dashboardList));
                });
    }

    // --- API Consumption: Tenants ---
    public CompletionStage<Result> tenants() {
        String apiUrl = "http://localhost/rentpulse-api/tenants/list_tenants.php";

        return ws.url(apiUrl)
                .addHeader("X-App-Username", "rentpulse_app_001")
                .addHeader("X-App-Password", "b99marasighan/X")
                .get()
                .thenApply(response -> {
                    String body = response.getBody();
                    List<Map<String, Object>> tenantList = new ArrayList<>();
                    try {
                        JsonNode root = mapper.readTree(body);
                        JsonNode jsonNode = root.get("data");
                        if (jsonNode == null && root.isArray()) jsonNode = root;
                        if (jsonNode != null && jsonNode.isArray()) {
                            for (JsonNode node : jsonNode) tenantList.add(mapper.convertValue(node, Map.class));
                        }
                    } catch (Exception e) { e.printStackTrace(); }
                    return ok(views.html.tenants.render(tenantList));
                });
    }

    // --- API Consumption: Properties ---
    public CompletionStage<Result> properties() {
        String apiUrl = "http://localhost/rentpulse-api/properties/get_properties.php";

        return ws.url(apiUrl)
                .addHeader("X-App-Username", "rentpulse_app_001")
                .addHeader("X-App-Password", "b99marasighan/X")
                .get().thenApply(response -> {
                    String body = response.getBody();
                    List<Map<String, Object>> properties = new ArrayList<>();
                    try {
                        JsonNode root = mapper.readTree(body);
                        JsonNode jsonNode = root.get("data");
                        if (jsonNode == null && root.isArray()) jsonNode = root;
                        if (jsonNode != null && jsonNode.isArray()) {
                            for (JsonNode node : jsonNode) properties.add(mapper.convertValue(node, Map.class));
                        }
                    } catch (Exception e) { e.printStackTrace(); }
                    return ok(views.html.properties.render(properties));
                });
    }

    // --- API Consumption: Users ---
    public CompletionStage<Result> users() {
        String apiUrl = "http://localhost/rentpulse-api/users/list_users.php";

        return ws.url(apiUrl)
                .addHeader("X-App-Username", "rentpulse_app_001")
                .addHeader("X-App-Password", "b99marasighan/X")
                .get()
                .thenApply(response -> {
                    String body = response.getBody();
                    List<Map<String, Object>> userList = new ArrayList<>();
                    try {
                        JsonNode root = mapper.readTree(body);
                        JsonNode jsonNode = root.get("data");
                        if (jsonNode == null && root.isArray()) jsonNode = root;
                        if (jsonNode != null && jsonNode.isArray()) {
                            for (JsonNode node : jsonNode) userList.add(mapper.convertValue(node, Map.class));
                        }
                    } catch (Exception e) { e.printStackTrace(); }
                    return ok(views.html.users.render(userList));
                });
    }

    // --- API Consumption: Analytics ---
    public CompletionStage<Result> analyticsData() {
        String rentTrendUrl = "http://localhost/rentpulse-api/analytics/rent-trend.php";
        String occupancyUrl = "http://localhost/rentpulse-api/analytics/occupancy-rate.php";
        String incomeUrl = "http://localhost/rentpulse-api/analytics/income-distribution.php";

        return ws.url(rentTrendUrl)
                .addHeader("X-App-Username", "rentpulse_app_001")
                .addHeader("X-App-Password", "b99marasighan/X")
                .get()
                .thenCompose(rentTrendResp -> {
                    String rentTrendBody = rentTrendResp.getBody();
                    return ws.url(occupancyUrl)
                            .addHeader("X-App-Username", "rentpulse_app_001")
                            .addHeader("X-App-Password", "b99marasighan/X")
                            .get()
                            .thenCompose(occupancyResp -> {
                                String occupancyBody = occupancyResp.getBody();
                                return ws.url(incomeUrl)
                                        .addHeader("X-App-Username", "rentpulse_app_001")
                                        .addHeader("X-App-Password", "b99marasighan/X")
                                        .get()
                                        .thenApply(incomeResp -> {
                                            String incomeBody = incomeResp.getBody();
                                            List<Map<String, Object>> analyticsList = new ArrayList<>();
                                            try {
                                                JsonNode rentTrendJson = mapper.readTree(rentTrendBody);
                                                JsonNode occupancyJson = mapper.readTree(occupancyBody);
                                                JsonNode incomeJson = mapper.readTree(incomeBody);

                                                Map<String, Object> combinedAnalytics = Map.of(
                                                        "rentTrend", mapper.convertValue(rentTrendJson, Map.class),
                                                        "occupancy", mapper.convertValue(occupancyJson, Map.class),
                                                        "income", mapper.convertValue(incomeJson, Map.class)
                                                );

                                                analyticsList.add(combinedAnalytics);
                                            } catch (Exception e) { e.printStackTrace(); }
                                            return ok(views.html.analytics.render(analyticsList));
                                        });
                            });
                });
    }

    // --- API Consumption: Landlords ---
    public CompletionStage<Result> landlords() {
        String apiUrl = "http://localhost/rentpulse-api/landlords/get_all_landlords.php";

        return ws.url(apiUrl)
                .addHeader("X-App-Username", "rentpulse_app_001")
                .addHeader("X-App-Password", "b99marasighan/X")
                .get()
                .thenApply(response -> {
                    String body = response.getBody();
                    List<Map<String, Object>> landlordList = new ArrayList<>();
                    try {
                        JsonNode root = mapper.readTree(body);
                        JsonNode jsonNode = root.get("data");
                        if (jsonNode == null && root.isArray()) jsonNode = root;
                        if (jsonNode != null && jsonNode.isArray()) {
                            for (JsonNode node : jsonNode) landlordList.add(mapper.convertValue(node, Map.class));
                        }
                    } catch (Exception e) { e.printStackTrace(); }
                    return ok(views.html.landlords.render(landlordList));
                });
    }

    // --- Static pages ---
    public Result payments() { return ok(payments.render()); }
    public Result messages() { return ok(messages.render()); }
    public Result settings() { return ok(settings.render()); }
    public Result support() { return ok(support.render()); }
    public Result extras() { return ok(extras.render()); }

}
