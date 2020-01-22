package com.wwt.managemail.controller;

import com.wwt.managemail.service.BankMyProductService;
import com.wwt.managemail.vo.BankMyProductQueryVO;
import com.wwt.managemail.vo.BankMyProductVo;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.List;

@RestController
@RequestMapping("/excel")
public class ExcelExportController extends BaseController {
    @Autowired
    BankMyProductService bankMyProductService;

    public static void downLoadExcel(String fileName, HttpServletResponse response, Workbook workbook) {
        try {
            response.setCharacterEncoding("UTF-8");
            response.setHeader("content-Type", "application/vnd.ms-excel");
            response.setHeader("Content-Disposition",
                    "attachment;filename=\"" + URLEncoder.encode(fileName, "UTF-8") + "\"");
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/excelExport")
    public ResponseEntity<Resource> excel2007Export(HttpServletResponse response) {
        try {
            ClassPathResource cpr = new ClassPathResource("/templates/" + "investment.xlsx");
            InputStream is = cpr.getInputStream();
            Workbook workbook = new XSSFWorkbook(is);
            BankMyProductQueryVO bankMyProductQueryVO = new BankMyProductQueryVO();
            List<BankMyProductVo> list = bankMyProductService.select(bankMyProductQueryVO);
            org.apache.poi.ss.usermodel.Sheet sheet0 = workbook.getSheetAt(0);
            //这里作为演示，造几个演示数据，模拟数据库里查数据
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

            for (int i = 0; i < list.size(); i++) {
                BankMyProductVo vo = list.get(i);
                //从第三行开始填充数据
                Row row = sheet0.createRow(i + 1);
                int count = 1;
                row.createCell(count).setCellValue(vo.getId());
                row.createCell(count++).setCellValue(vo.getBank().getName());
                row.createCell(count++).setCellValue(vo.getBank().getBankName());
                row.createCell(count++).setCellValue(vo.getBank().getBankCard());
                row.createCell(count++).setCellValue(vo.getProductType());
                row.createCell(count++).setCellValue(vo.getInvestmentAmount().doubleValue());

                row.createCell(count++).setCellValue(df.format(vo.getBuyingTime()));
                row.createCell(count++).setCellValue(df.format(vo.getInterestStartTime()));
                row.createCell(count++).setCellValue(df.format(vo.getProfitDate()));
                row.createCell(count++).setCellValue(df.format(vo.getDueTime()));
                row.createCell(count++).setCellValue(vo.getExpectedInterestRate().doubleValue());
                row.createCell(count++).setCellValue(vo.getInterestRate().doubleValue());
                row.createCell(count++).setCellValue(vo.getInterestPaymentMethod());

                row.createCell(count++).setCellValue(vo.getExpectedInterestIncomeMonth().doubleValue());
                row.createCell(count++).setCellValue(vo.getExpectedInterestIncomeTotal().doubleValue());
                row.createCell(count++).setCellValue(vo.getTotalEffectiveInterestIncome().doubleValue());
                row.createCell(count++).setCellValue(vo.getPrincipalAndInterestIncome().doubleValue());

            }
            String fileName = "投资明细.xlsx";
            downLoadExcel(fileName, response, workbook);

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new ResponseEntity<Resource>(HttpStatus.OK);
    }
}
