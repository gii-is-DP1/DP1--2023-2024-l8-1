package org.springframework.samples.petclinic.game;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;

@WebMvcTest(value = {
        GameRestController.class }, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, 
        classes = WebSecurityConfigurer.class))
public class GameControllerTests {
    
    private static final int TEST_PLAYER_ID = 1;
	private static final String BASE_URL = "/api/v1/game";



}
