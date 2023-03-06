import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.TimeZone;

@WebFilter("/time")
public class TimezoneValidateFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        String timeZone = req.getParameter("timezone");
        if (timeZone != null) {
            String[] availableTimeZones = TimeZone.getAvailableIDs();
            for (String availableTimeZone : availableTimeZones
            ) {
                if (availableTimeZone.equalsIgnoreCase(timeZone)) {
                    chain.doFilter(req, res);
                } else {
                    res.setStatus(400);
                    res.setContentType("application/json");
                    res.getWriter().write("{\"Error\": \"Invalid timezone\"}");
                    res.getWriter().close();
                }
            }
        }
    }
}