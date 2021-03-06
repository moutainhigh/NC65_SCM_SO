package nc.pubitf.so.m32.sr.formula;

import java.io.Serializable;

import nc.vo.pubapp.AppContext;
import nc.vo.pubapp.pattern.pub.SqlBuilder;
import nc.vo.so.pub.SOTable;
import nc.vo.so.pub.enumeration.BillStatus;

import nc.pubitf.sr.formula.ISRFormulaSqlMap;
import nc.pubitf.sr.formula.ISRFormulaSqlPara;

import nc.impl.pubapp.pattern.database.IDExQueryBuilder;

/**
 * 销售发票返利函数sql字段映射类
 * 
 * @since 6.1
 * @version 2012-11-27 15:43:23
 * @author 冯加彬
 */
public class SaleInvoiceFormulaSqlMap implements ISRFormulaSqlMap, Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 3526353467562739827L;

  private String sumkey;

  private ISRFormulaSqlPara para;

  /**
   * 构造子
   * 
   * @param sumkey
   * @param para
   */
  public SaleInvoiceFormulaSqlMap(String sumkey, ISRFormulaSqlPara para) {
    this.sumkey = sumkey;
    this.para = para;
  }

  @Override
  public String getFrom() {
    SqlBuilder from = new SqlBuilder();
    from.append("so_saleinvoice inner join so_saleinvoice_b ");
    from.append("on so_saleinvoice.csaleinvoiceid = so_saleinvoice_b.csaleinvoiceid");
    // 结算方式
    String[] baltype = this.para.getBaltype();
    // 价格项
    String[] priceitem = this.para.getPriceitem();
    // 促销类型
    String[] prcPromotTypeIDs = this.para.getCprcpromottypeid();
    if (null != priceitem && priceitem.length > 0 || null != prcPromotTypeIDs
        && prcPromotTypeIDs.length > 0) {
      from.append(" inner join so_saleorder on ");
      from.append("so_saleinvoice_b.cfirstid = so_saleorder.csaleorderid");
      from.append(" inner join so_saleorder_b on ");
      from.append("so_saleinvoice_b.cfirstbid = so_saleorder_b.csaleorderbid ");
    }
    else if (null != baltype && baltype.length > 0) {
      from.append(" inner join so_saleorder on ");
      from.append("so_saleinvoice_b.cfirstid = so_saleorder.csaleorderid");
    }
    return from.toString();
  }

  @Override
  public String getWhere() {
    SqlBuilder where = new SqlBuilder();
    this.appendParaWhere(where);
    this.appendFixWhere(where);
    return where.toString();
  }

  private void appendFixWhere(SqlBuilder where) {

    // 只有审核态
    where.append(" and ");
    where.append("so_saleinvoice.fstatusflag", BillStatus.AUDIT);
    // 非赠品
    where.append(" and ");
    where.append(" isnull(so_saleinvoice_b.blargessflag, 'N') = 'N' ");

    String pk_group = AppContext.getInstance().getPkGroup();
    where.append(" and ");
    where.append(" so_saleinvoice.pk_group", pk_group);
    where.append(" and ");
    where.append(" so_saleinvoice_b.pk_group", pk_group);
    where.append(" and ");
    where.append(" so_saleinvoice.dr ", 0);
    where.append(" and ");
    where.append(" so_saleinvoice_b.dr ", 0);

  }

  private void appendParaWhere(SqlBuilder where) {
    // 销售组织
    String[] saleorg = this.para.getSaleorg();
    IDExQueryBuilder iq =
        new IDExQueryBuilder(SOTable.TMP_SO_SALEORG.getName());
    where.append(iq.buildSQL("so_saleinvoice_b.csaleorgid", saleorg));
    // 开始日期
    String begindate = this.para.getDbegindate().toString();
    where.append(" and ");
    where.append("so_saleinvoice.dbilldate", ">=", begindate);
    where.append(" and ");
    where.append("so_saleinvoice_b.dbilldate", ">=", begindate);
    // 结束日期
    String enddate = this.para.getDenddate().toString();
    where.append(" and ");
    where.append("so_saleinvoice.dbilldate", "<=", enddate);
    where.append(" and ");
    where.append("so_saleinvoice_b.dbilldate", "<=", enddate);
    // 订单类型
    String[] ordertype = this.para.getOrdertype();
    if (null != ordertype && ordertype.length > 0) {
      where.append(" and ");
      iq = new IDExQueryBuilder(SOTable.TMP_SO_ORDERTYPE.getName());
      where.append(iq.buildSQL("so_saleinvoice_b.vfirsttrantype", ordertype));
    }
    // 订单客户
    String[] ordercust = this.para.getOrdercust();
    if (null != ordercust && ordercust.length > 0) {
      where.append(" and ");
      iq = new IDExQueryBuilder(SOTable.TMP_SO_ORDERCUST.getName());
      where.append(iq.buildSQL("so_saleinvoice_b.cordercustid", ordercust));
    }
    // 开票客户
    String[] invoicecust = this.para.getInvoicecust();
    if (null != invoicecust && invoicecust.length > 0) {
      where.append(" and ");
      iq = new IDExQueryBuilder(SOTable.TMP_SO_INVCUST.getName());
      where.append(iq.buildSQL("so_saleinvoice.cinvoicecustid", invoicecust));
    }
    // 币种
    String currency = this.para.getCcurrencyid();
    where.append(" and ");
    where.append("so_saleinvoice.corigcurrencyid", currency);
    // 结算方式
    String[] baltype = this.para.getBaltype();
    if (null != baltype && baltype.length > 0) {
      where.append(" and ");
      iq = new IDExQueryBuilder(SOTable.TMP_SO_BALTYPE.getName());
      where.append(iq.buildSQL("so_saleorder.cbalancetypeid", baltype));
    }
    // 价格项
    String[] priceitem = this.para.getPriceitem();
    if (null != priceitem && priceitem.length > 0) {
      where.append(" and ");
      iq = new IDExQueryBuilder(SOTable.TMP_SO_PRCITEM.getName());
      where.append(iq.buildSQL("so_saleorder_b.cpriceitemid", priceitem));
    }
    // 促销类型ID数组
    String[] prcPromotTypeIDs = this.para.getCprcpromottypeid();
    // 促销类型ID数组不为空的时候进行过滤
    if (null != prcPromotTypeIDs && prcPromotTypeIDs.length > 0) {
      where.append(" and ");
      iq = new IDExQueryBuilder(SOTable.TMP_SO_PRCPROMTYPE.getName());
      where.append(iq.buildSQL("so_saleorder_b.cprcpromottypeid",
          prcPromotTypeIDs));
    }

  }

  @Override
  public String getSumKey() {
    return this.sumkey;
  }

  @Override
  public String getSaleOrgKey() {
    return "so_saleinvoice_b.csaleorgid";
  }

  @Override
  public String getSettleOrgKey() {
    return "so_saleinvoice.pk_org";
  }

  @Override
  public String getOrderCustKey() {
    return "so_saleinvoice_b.cordercustid";
  }

  @Override
  public String getInvoiceCustKey() {
    return "so_saleinvoice.cinvoicecustid";
  }

  @Override
  public String getMaterialOIDKey() {
    return "so_saleinvoice_b.cmaterialid";
  }
}
