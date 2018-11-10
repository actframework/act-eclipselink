package test;

import act.controller.annotation.UrlContext;
import act.db.DbBind;
import act.db.jpa.JPADao;
import act.util.PropertySpec;
import act.util.SimpleBean;
import org.osgl.mvc.annotation.GetAction;
import org.osgl.mvc.annotation.PostAction;

import javax.persistence.*;

@Entity(name = "orderX")
public class Order implements SimpleBean {
    @Id
    @GeneratedValue(generator = "system-uuid")
    public String id;

    public String name;

    @UrlContext("orders")
    public static class Dao extends JPADao<String, Order> {

        @PostAction
        @PropertySpec("id")
        public Order create(Order order) {
            return save(order);
        }

        @GetAction("{order}")
        public Order get(@DbBind Order order) {
            return order;
        }

    }

}
