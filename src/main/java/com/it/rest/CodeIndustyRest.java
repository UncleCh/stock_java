package com.it.rest;

import com.it.bean.CodeObserver;
import com.it.repository.h2.CodeObserverMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * @author chenxj
 */
@RestController
@RequestMapping("/rest")
public class CodeIndustyRest {

    // INSERT INTO `code_observer` (`stock_name`, `close_price`, `incr_per`, `pe`, `rec_peg`, `ten_day`, `industry`, `detail_industry`, `last_close_price`, `code`, `market`)
    // VALUES ('牧原股份', '54.90', '17.04', '37.15', '1.780', '-0.030', '农业', '养殖', '55.70', '002714', 'sz');



//

    @Autowired
    CodeObserverMapper codeObserverMapper;

    @RequestMapping(method = RequestMethod.POST, value = "/query")
    public Object execute(@RequestBody CodeObserver codeObserver, HttpServletResponse resp) {
        return codeObserverMapper.getCodeObserverList(codeObserver);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/update")
    public void update(@RequestBody CodeObserver codeObserver) {
        codeObserverMapper.updateCodeObserver(codeObserver);
    }

}