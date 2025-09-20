package io.github.qifan777.server.product.root.entity;

import io.github.qifan777.server.infrastructure.jimmer.BaseEntity;
import io.github.qifan777.server.infrastructure.model.UploadFile;
import io.github.qifan777.server.product.category.entity.ProductCategory;
import io.github.qifan777.server.product.root.model.KeyValue;
import io.qifan.infrastructure.generator.core.*;
import jakarta.validation.constraints.Null;
import org.babyfish.jimmer.Formula;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.IdView;
import org.babyfish.jimmer.sql.ManyToOne;
import org.babyfish.jimmer.sql.Serialized;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品表
 */
@GenEntity
@Entity
public interface Product extends BaseEntity {

    /**
     * 名称
     */
    @GenTextField(label = "名称", order = 0)
    String name();

    /**
     * 价格
     */
    @GenNumberField(label = "价格", order = 1)
    BigDecimal price();

    /**
     * 库存
     */
    @GenNumberField(label = "库存", order = 2)
    int stock();

    /**
     * 封面
     */
    @GenImageField(label = "封面", order = 3)
    String cover();

    /**
     * 品牌
     */
    @GenTextField(label = "品牌", order = 4)
    @Null
    String brand();

    /**
     * 类别id
     */
    @GenAssociationField(label = "类别", order = 5, prop = "categoryId")
    @ManyToOne
    ProductCategory category();

    @IdView
    String categoryId();

    /**
     * 描述
     */
    @GenTextAreaField(label = "描述", order = 6)
    String description();

    /**
     * 标签
     */
    @Null
    @Serialized
    @GenValueField(label = "标签", order = 7)
    List<String> tags();

    /**
     * 规格
     */
    @Null
    @Serialized
    @GenKeyValueField(label = "规格", order = 8)
    List<KeyValue> specifications();

    /**
     * 属性
     */
    @Null
    @Serialized
    @GenKeyValueField(label = "属性", order = 9)
    List<KeyValue> attributes();

    @Null
    @Serialized
    List<UploadFile> pictures();

    @Formula(sql = """
            (select count(1)
             from product_order_item t
                      left join product_order t1 on t.product_order_id = t1.id
             where t.product_id = %alias.id
               and t1.status = 'FINISHED')
            """)
    Integer sell();
}

