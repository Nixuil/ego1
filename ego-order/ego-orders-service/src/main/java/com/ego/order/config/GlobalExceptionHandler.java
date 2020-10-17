package com.ego.order.config;

import com.ego.common.exception.EgoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

/**
 * 〈〉
 *
 * @author coach tam
 * @email 327395128@qq.com
 * @create 2020/9/22
 * @since 1.0.0
 * 〈坚持灵活 灵活坚持〉
 */
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<HashMap<String,Object>> exceptionHandler(HttpServletRequest request, Exception e){
        //打印完成错误信息，方便调试
        e.printStackTrace();
        //检测是否是自定义异常，如果是响应详细信息
        if(e instanceof EgoException){
            EgoException exception=(EgoException) e;
            //利用Map实现VO(也可以自定义VO)
            HashMap<String, Object> resultVO = new HashMap<>();
            resultVO.put("errorCode", exception.getErrorCode());
            resultVO.put("errorMessage", exception.getErrorMessage());
            //将具体错误信息响应给客户端
            return ResponseEntity.status(exception.getErrorCode()).body(resultVO);
        }
        // 如果不是，直接返回500
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

}
