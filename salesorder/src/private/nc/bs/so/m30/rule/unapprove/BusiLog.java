package nc.bs.so.m30.rule.unapprove;

import java.util.ArrayList;
import java.util.List;

import nc.vo.pub.BusinessException;
import nc.vo.pub.IVOMeta;
import nc.vo.pubapp.AppContext;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.so.m30.entity.SaleOrderVO;

import nc.itf.scmpub.reference.uap.md.MDQueryFacade;

import nc.md.model.IBean;

import nc.bs.busilog.util.BusinessLogServiceUtil;
import nc.bs.busilog.vo.BusinessLogContext;
import nc.bs.logging.Logger;
import nc.bs.sm.busilog.util.LogConfigServiceFacade;

import nc.impl.pubapp.pattern.rule.IRule;

/**
 * @description
 * 取消审批前业务日志配置规则
 * @scene
 * 销售订单取消审批前
 * @param
 * 无
 */
public class BusiLog implements IRule<SaleOrderVO> {

  @Override
  public void process(SaleOrderVO[] vos) {
    List<BusinessLogContext> list = new ArrayList<BusinessLogContext>();
    IVOMeta meta = vos[0].getParentVO().getMetaData();
    String beanname = meta.getEntityName();
    IBean bean = MDQueryFacade.getBeanByFullName(beanname);
    for (SaleOrderVO vo : vos) {
      BusinessLogContext context = new BusinessLogContext();
      context.setPk_busiobj(vo.getPrimaryKey());
      context.setBusiobjcode(vo.getParentVO().getVbillcode());
      // context.setBusiobjname(aggCtSaleVO.getParentVO()..getCtname());
      context.setTypepk_busiobj(bean.getID());
      context.setPk_operation("2accf4fe-2b69-4653-b31a-03f23a2b16bd");
      context.setHasjudged(true);
      // context.setOldbusiobjvo(vo);//.setBusiobjvo(vo);
      context.setOrgpk_busiobj(vos[0].getParentVO().getPk_org());
      if (this.isNeedBusiLog(AppContext.getInstance().getPkGroup(),
          bean.getID(), context.getPk_operation())) {
        list.add(context);
      }
    }
    try {
      BusinessLogServiceUtil.insertBatchBusilogInTransaction(list);
    }
    catch (BusinessException e) {
      ExceptionUtils.wrappException(e);
    }
  }

  private boolean isNeedBusiLog(String grouppk, String metaid, String busiopid) {
    try {
      return LogConfigServiceFacade.getInstance().isOperNeedLog(grouppk,
          metaid, busiopid);
    }
    catch (BusinessException e) {
      Logger.error(e.getMessage(), e);
      return false;
    }
  }
}
