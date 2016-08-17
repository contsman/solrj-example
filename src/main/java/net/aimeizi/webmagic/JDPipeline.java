package net.aimeizi.webmagic;

import net.aimeizi.dao.JDProductDao;
import net.aimeizi.domain.Product;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/10/12.
 */
@Component("jdPipeline")
public class JDPipeline implements Pipeline {
    private static int num = 0;
    @Autowired
    JDProductDao productDao;

    public void process(ResultItems resultItems, Task task) {
        Map<String, Object> items = resultItems.getAll();
        if (resultItems != null && resultItems.getAll().size() > 0) {
            List<String> names = (List<String>) items.get("names");
            List<String> prices = (List<String>) items.get("prices");
            List<String> comments = (List<String>) items.get("comments");
            List<String> links = (List<String>) items.get("links");
            List<String> pics = (List<String>) items.get("pics");
            String category = (String) items.get("category");
            for (int i = 0; i < names.size(); i++) {
                Product p = new Product();
                p.setName(names.get(i));
                p.setPic(pics.get(i));
                try{
                    double price = Double.parseDouble(prices.get(i));
                    p.setPrice(price);
                }catch (Exception e){
                }
                try{
                    String commentstr = comments.get(i);
                    long commentl = StringUtils.isNotEmpty(commentstr)?(commentstr.replace("+","").contains("Íò")?Long.valueOf(commentstr.replace("+","").replace("Íò",""))*1000:Long.valueOf(commentstr.replace("+","").replace("Íò",""))):0;
                    p.setComment(commentl);
                }catch (Exception e){
                }
                p.setUrl(links.get(i));
                p.setCategory(category);
                p.setCreate(new Date());
                p.setUpdate(new Date());
                productDao.save(p);
                num ++;
                System.out.println("ProductName: "+p.getName()+",Url: "+p.getUrl());
                System.out.println("total: "+num);
            }
        }
    }

}
