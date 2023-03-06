import org.apache.taglibs.standard.extra.spath.ParseException;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

@WebServlet(urlPatterns = "/time")
public class TimeServlet extends HttpServlet {
    private TemplateEngine engine;

    @Override
    public void init() {
        engine = new TemplateEngine();
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("mytemplates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setOrder(engine.getTemplateResolvers().size());
        resolver.setCacheable(false);
        engine.addTemplateResolver(resolver);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String timezone = req.getParameter("timezone");
        String currentTime = "";
        Cookie[] cookies = req.getCookies();

        if (timezone == null && cookies == null) {
            timezone = "UTC";
        } else if(timezone == null && cookies != null) {
            int length = cookies.length;
            String cookie = cookies[length - 1].getValue();
            TimeZone systemTimezone = TimeZone.getTimeZone(cookie);
            timezone = systemTimezone.getID();
        }
        try {
            currentTime = getCurrentTimeWithTimezone(timezone);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        resp.addCookie(new Cookie("timezone" , timezone));
        Context context = new Context(
                req.getLocale(),
                Map.of("time", currentTime)
        );
        engine.process("timezone", context, resp.getWriter());
        resp.getWriter().close();
    }

    public static String getCurrentTimeWithTimezone(String zone) throws ParseException {
        Date date = new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone(zone));
        return df.format(date).concat(" " + zone);
    }

}
