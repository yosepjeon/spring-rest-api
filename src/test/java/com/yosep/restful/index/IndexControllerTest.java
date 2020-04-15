package com.yosep.restful.index;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import com.yosep.restful.common.BaseControllerTest;

//@RunWith(SpringRunner.class)
////@WebMvcTest
//@SpringBootTest
//@AutoConfigureMockMvc
//@AutoConfigureRestDocs
//@Import(RestDocsConfiguration.class) // 다른 스프링 빈 설정파일을 읽어와서 사용하는 방법 중 하나.
//@ActiveProfiles("test")
public class IndexControllerTest extends BaseControllerTest{
	@Autowired
	MockMvc mockMvc;

	@Test
	public void index() throws Exception {
		this.mockMvc.perform(get("/api/")).andExpect(status().isOk()).andExpect(jsonPath("_links.events").exists());
	}
}
