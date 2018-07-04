package nc.ui.so.salequotation.handler.body;

import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pubapp.uif2app.event.card.CardBodyAfterEditEvent;
import nc.ui.so.pub.keyvalue.CardKeyValue;
import nc.ui.so.pub.util.BodyEditEventUtil;
import nc.ui.so.salequotation.pub.SalequoCalculator;
import nc.vo.so.pub.keyvalue.IKeyRela;
import nc.vo.so.pub.keyvalue.IKeyValue;
import nc.vo.so.pub.rule.SOBuysellTriaRule;
import nc.vo.so.pub.rule.SOCalConditionRule;
import nc.vo.so.pub.rule.SOTaxInfoRule;
import nc.vo.so.pub.util.ArrayUtil;
import nc.vo.so.salequotation.entity.SalequotationBVO;
import nc.vo.so.salequotation.keyrela.SalequoKeyRela;

public class ReceCountryEditHandler {

  public void afterEdit(CardBodyAfterEditEvent e) {

    int[] rows = BodyEditEventUtil.getInstance().getAfterEditRow(e);
    BillCardPanel cardPanel = e.getBillCardPanel();
    IKeyValue keyValue = new CardKeyValue(cardPanel);
    IKeyRela keyRela = new SalequoKeyRela();
    SalequoCalculator calculator = new SalequoCalculator(cardPanel);

    // �Զ����ù�������/����ó��ƥ�����
    SOBuysellTriaRule buysellflag = new SOBuysellTriaRule(keyValue);
    buysellflag.setBuysellAndTriaFlag(rows);
    int[] chgBuysellRows = buysellflag.getBuysellChgRow();
    calculator.calculate(chgBuysellRows, SOCalConditionRule.getCalPriceKey());    	


    // ѯ˰
    SOTaxInfoRule taxInfo = new SOTaxInfoRule(keyValue, keyRela);
    taxInfo.setTaxInfoByBodyPos(rows);
    int[] chgTaxinfoRows = taxInfo.getTaxChangeRows();
    calculator.calculate(chgTaxinfoRows, SalequotationBVO.NTAXRATE);    	
  }
}