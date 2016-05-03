package spark.route;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Enclosed.class)
public class RoutesTest {

    public static class AddRouteTests {
        @Test
        public void testParseValidateAddRoute_whenHttpMethodIsValid_thenAddRoute() {
            //given
            String route = "get'/hello'";
            String acceptType = "*/*";
            Object target = new Object();

            RouteEntry expectedRouteEntry = new RouteEntry();
            expectedRouteEntry.acceptedType = acceptType;
            expectedRouteEntry.httpMethod = HttpMethod.get;
            expectedRouteEntry.path = "/hello";
            expectedRouteEntry.target = target;
            List<RouteEntry> expectedRoutes = new ArrayList<>();
            expectedRoutes.add(expectedRouteEntry);

            Routes simpleRouteMatcher = Routes.create();
            simpleRouteMatcher.add(route, acceptType, target);

            //then
            List<RouteEntry> routes = Whitebox.getInternalState(simpleRouteMatcher, "routes");
            assertTrue("Should return true because http method is valid and the route should be added to the list",
                       Util.equals(routes, expectedRoutes));

        }

        @Test
        public void testParseValidateAddRoute_whenHttpMethodIsInvalid_thenDoNotAddRoute() {
            //given
            String route = "test'/hello'";
            String acceptType = "*/*";
            Object target = new Object();

            Routes simpleRouteMatcher = Routes.create();
            simpleRouteMatcher.add(route, acceptType, target);

            //then
            List<RouteEntry> routes = Whitebox.getInternalState(simpleRouteMatcher, "routes");
            assertEquals("Should return 0 because test is not a valid http method, so the route is not added to the list",
                         routes.size(), 0);
        }
    }

    public static class RemoveRouteTests {
        List<RouteEntry> expectedRoutes;
        Routes simpleRouteMatcher;

        @Before
        public void setup() {
            String[] route = new String[] {"get'/hello'", "post'/hello'", "get'/bye'"};
            String acceptType = "*/*";
            Object target = new Object();

            RouteEntry expectedRouteEntry = new RouteEntry();
            expectedRouteEntry.acceptedType = acceptType;
            expectedRouteEntry.httpMethod = HttpMethod.get;
            expectedRouteEntry.path = "/hello";
            expectedRouteEntry.target = target;
            expectedRoutes = new ArrayList<>();
            expectedRoutes.add(expectedRouteEntry);
            expectedRouteEntry = new RouteEntry(expectedRouteEntry);
            expectedRouteEntry.httpMethod = HttpMethod.post;
            expectedRouteEntry.path = "/hello";
            expectedRoutes.add(expectedRouteEntry);
            expectedRouteEntry = new RouteEntry(expectedRouteEntry);
            expectedRouteEntry.httpMethod = HttpMethod.get;
            expectedRouteEntry.path = "/bye";
            expectedRoutes.add(expectedRouteEntry);

            simpleRouteMatcher = Routes.create();
            simpleRouteMatcher.add(route[0], acceptType, target);
            simpleRouteMatcher.add(route[1], acceptType, target);
            simpleRouteMatcher.add(route[2], acceptType, target);
        }

        @Test
        public void testRemoveRoute() {

            simpleRouteMatcher.remove("bye");
            expectedRoutes.remove(2);

            List<RouteEntry> routes = Whitebox.getInternalState(simpleRouteMatcher, "routes");
            assertTrue(Util.equals(routes, expectedRoutes));
        }

        @Test
        public void testMultipleRemoveRoutes() {

            simpleRouteMatcher.remove("hello");
            expectedRoutes.remove(0);
            expectedRoutes.remove(0);

            List<RouteEntry> routes = Whitebox.getInternalState(simpleRouteMatcher, "routes");
            assertTrue(Util.equals(routes, expectedRoutes));
        }

        @Test
        public void testRemoveGetRoute() {

            simpleRouteMatcher.remove("hello", "get");
            expectedRoutes.remove(0);

            List<RouteEntry> routes = Whitebox.getInternalState(simpleRouteMatcher, "routes");
            assertTrue(Util.equals(routes, expectedRoutes));
        }

        @Test
        public void testRemoveInvalidRoute() {

            simpleRouteMatcher.remove("test");

            List<RouteEntry> routes = Whitebox.getInternalState(simpleRouteMatcher, "routes");
            assertTrue(Util.equals(routes, expectedRoutes));
        }
    }
}
