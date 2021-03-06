package nc.pubimpl.so.custmatrel.rule;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nc.itf.scmpub.reference.uap.bd.customer.CustomerBaseClassPubService;
import nc.itf.scmpub.reference.uap.bd.customer.CustomerPubService;
import nc.itf.scmpub.reference.uap.bd.customer.CustomerSaleClassPubService;
import nc.pubitf.so.custmatrel.CustMatRelParaVO;
import nc.vo.bd.accessor.IBDData;
import nc.vo.bd.cust.CustomerVO;
import nc.vo.bd.cust.saleinfo.CustsaleVO;
import nc.vo.so.pub.util.ObjectUtil;

/**
 * 客户与物料关系：扩展客户信息
 * 
 * @since 6.0
 * @version 2011-4-14 下午06:38:55
 * @author 祝会征
 */
public class CustMatRelCustExtendRule {
  /**
   * 扩展客户基本信息
   * 
   * @param pk_saleorg
   * @param extendpara
   */
  public void extendCustBaseClass(String pk_saleorg,
      List<CustMatRelParaVO> extendpara) {
    Set<String> setCustid = new HashSet<String>();
    for (CustMatRelParaVO para : extendpara) {
      setCustid.add(para.getPk_customer());
    }
    String[] pks = new String[setCustid.size()];
    setCustid.toArray(pks);
    Map<String, CustomerVO> custMap = this.getBaseCustInfo(pks);
    if (null == custMap || custMap.size() == 0) {
      return;
    }
    Map<String, String[]> tempMap = new HashMap<String, String[]>();
    CustMatRelParaVO[] paras = new CustMatRelParaVO[extendpara.size()];
    extendpara.toArray(paras);
    for (CustMatRelParaVO para : paras) {
      String custid = para.getPk_customer();
      if (tempMap.containsKey(custid)) {
        String[] baseclids = tempMap.get(custid);
        for (String basecl : baseclids) {
          CustMatRelParaVO basclextend =
              (CustMatRelParaVO) ObjectUtil.serializableClone(para);
          basclextend.setPk_customer(null);
          basclextend.setPk_custbaseclass(basecl);
          extendpara.add(basclextend);
        }
      }
      else {
        String pk_custcl = custMap.get(custid).getPk_custclass();
        String[] custclids = this.getFatherCustBaseClass(pk_saleorg, pk_custcl);
        for (String custcl : custclids) {
          CustMatRelParaVO basclextend =
              (CustMatRelParaVO) ObjectUtil.serializableClone(para);
          basclextend.setPk_customer(null);
          basclextend.setPk_custbaseclass(custcl);
          extendpara.add(basclextend);
        }
        tempMap.put(custid, custclids);
      }
    }

  }

  /**
   * 扩展客户销售信息
   * 
   * @param pk_saleorg
   * @param extendpara
   */
  public void extendCustSaleClass(String pk_saleorg,
      List<CustMatRelParaVO> extendpara) {
    Set<String> setCustid = new HashSet<String>();
    for (CustMatRelParaVO para : extendpara) {
      setCustid.add(para.getPk_customer());
    }
    String[] custids = new String[setCustid.size()];
    setCustid.toArray(custids);
    Map<String, CustsaleVO> custMap = this.getCustSaleInfo(custids, pk_saleorg);
    if (null == custMap || custMap.size() == 0) {
      return;
    }
    Map<String, String[]> tempMap = new HashMap<String, String[]>();
    CustMatRelParaVO[] paras = new CustMatRelParaVO[extendpara.size()];
    extendpara.toArray(paras);
    for (CustMatRelParaVO para : paras) {
      String custid = para.getPk_customer();
      if (tempMap.containsKey(custid)) {
        String[] saleclids = tempMap.get(custid);
        for (String salecl : saleclids) {
          CustMatRelParaVO basclextend = (CustMatRelParaVO) para.clone();
          basclextend.setPk_customer(null);
          basclextend.setPk_custsaleclass(salecl);
          extendpara.add(basclextend);
        }
      }
      else {
        CustsaleVO salevo = custMap.get(custid);
        String pk_custsalecl = salevo.getPk_custsaleclass();
        String[] custclids =
            this.getFatherCustSaleClass(pk_saleorg, pk_custsalecl);
        for (String custcl : custclids) {
          CustMatRelParaVO basclextend = (CustMatRelParaVO) para.clone();
          basclextend.setPk_customer(null);
          basclextend.setPk_custsaleclass(custcl);
          extendpara.add(basclextend);
        }
        tempMap.put(custid, custclids);
      }
    }
  }

  private Map<String, CustomerVO> getBaseCustInfo(String[] pks) {
    String[] custkeys = new String[] {
      CustomerVO.PK_CUSTCLASS
    };

    Map<String, CustomerVO> mapCust =
        CustomerPubService.getCustomerVOByPks(pks, custkeys);
    return mapCust;

  }

  private Map<String, CustsaleVO> getCustSaleInfo(String[] pks, String pk_org) {
    String[] custkeys = new String[] {
      CustsaleVO.PK_CUSTSALECLASS
    };
    Map<String, CustsaleVO> mapCustSale =
        CustomerPubService.getCustSaleVOByPks(pks, pk_org, custkeys);
    return mapCustSale;
  }

  private String[] getFatherCustBaseClass(String pk_saleorg, String pk_custcl) {
    List<IBDData> fathercustcls =
        CustomerBaseClassPubService.queryCustClassFather(pk_saleorg, pk_custcl,
            true);
    String[] fathercls = new String[fathercustcls.size()];
    int i = 0;
    for (IBDData bddata : fathercustcls) {
      fathercls[i] = bddata.getPk();
      i++;
    }
    return fathercls;
  }

  private String[] getFatherCustSaleClass(String pk_saleorg,
      String pk_custsalecl) {
    List<IBDData> fatherdatas =
        CustomerSaleClassPubService.queryCustSaleClassFather(pk_saleorg,
            pk_custsalecl, true);
    String[] fathersalecls = new String[fatherdatas.size()];
    int i = 0;
    for (IBDData data : fatherdatas) {
      fathersalecls[i] = data.getPk();
      i++;
    }
    return fathersalecls;
  }
}
