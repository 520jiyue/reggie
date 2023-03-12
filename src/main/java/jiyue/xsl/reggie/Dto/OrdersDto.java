package jiyue.xsl.reggie.Dto;


import jiyue.xsl.reggie.Entity.OrderDetail;
import jiyue.xsl.reggie.Entity.Orders;
import lombok.Data;
import java.util.List;

@Data
public class OrdersDto extends Orders {

    private String userName;

    private String phone;

    private String address;

    private String consignee;

    private List<OrderDetail> orderDetails;
	
}
