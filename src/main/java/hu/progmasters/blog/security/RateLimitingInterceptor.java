package hu.progmasters.blog.security;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitingInterceptor implements HandlerInterceptor {

    private final ConcurrentHashMap<String, RequestCounter> requestCounts = new ConcurrentHashMap<>();
    private static final long RATE_LIMIT_WINDOW = 10 * 60 * 1000; //10 min
    private static final int MAX_REQUESTS = 4;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String ip = request.getRemoteAddr();

        RequestCounter counter = requestCounts.computeIfAbsent(ip, k -> new RequestCounter());

        if (System.currentTimeMillis() - counter.getCreationTime() > RATE_LIMIT_WINDOW) {
            counter.reset();
        }

        if (counter.incrementAndGet() > MAX_REQUESTS) {
            response.setStatus(429);
            return false;
        }

        return true;
    }
}
