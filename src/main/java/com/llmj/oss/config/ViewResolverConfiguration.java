package com.llmj.oss.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

@Configuration
public class ViewResolverConfiguration {
    @Configuration//用来定义 DispatcherServlet 应用上下文中的 bean
    @EnableWebMvc
    @ComponentScan("com.llmj.oss")
    public class WebConfig implements WebMvcConfigurer {
    	
    	@Autowired
    	private GlobalConfig global;
    	@Value("${upload.local.basePath}")
    	private String localPath;
    	@Value("${upload.local.logo}")
    	private String logoPath;
    	
        @Bean
        public ViewResolver viewResolver() {
            InternalResourceViewResolver resolver = new InternalResourceViewResolver();
            resolver.setPrefix("/WEB-INF/jsp/");
            resolver.setSuffix(".jsp");
            resolver.setViewNames("*");
            resolver.setOrder(2);
            return resolver;
        }
        
        /* 路径拦截 */
		@Override
		public void addInterceptors(InterceptorRegistry registry) {
			registry.addInterceptor(new UrlInterceptor(global)).addPathPatterns("/**")
					.excludePathPatterns("/static/**",IConsts.LOCALDOWN+"**",IConsts.LOGOPATH+"**");
		}

		@Bean
        public ITemplateResolver templateResolver() {
            SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
            templateResolver.setTemplateMode("HTML5");
            templateResolver.setPrefix("/WEB-INF/");
            templateResolver.setSuffix(".html");
            templateResolver.setCharacterEncoding("utf-8");
            templateResolver.setCacheable(false);
            return templateResolver;
        }

        @Bean
        public SpringTemplateEngine templateEngine() {
            SpringTemplateEngine templateEngine = new SpringTemplateEngine();
            templateEngine.setTemplateResolver(templateResolver());
            // templateEngine
            return templateEngine;
        }

        @Bean
        public ThymeleafViewResolver viewResolverThymeLeaf() {
            ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
            viewResolver.setTemplateEngine(templateEngine());
            viewResolver.setCharacterEncoding("utf-8");
            viewResolver.setOrder(1);
            viewResolver.setViewNames(new String[]{"html/*", "vue/*"});
            return viewResolver;
        }

        @Override
        public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
            configurer.enable();
        }

        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            //registry.addResourceHandler("/img/**").addResourceLocations("/WEB-INF/img/");
            registry.addResourceHandler("/static/**").addResourceLocations("/WEB-INF/static/");
            registry.addResourceHandler(IConsts.LOCALDOWN+"**").addResourceLocations("file:"+localPath);
            registry.addResourceHandler(IConsts.LOGOPATH+"**").addResourceLocations("file:"+logoPath);
        }
    }
}
