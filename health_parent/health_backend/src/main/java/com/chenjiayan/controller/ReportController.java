package com.chenjiayan.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.chenjiayan.constant.MessageConstant;
import com.chenjiayan.entitiy.Result;
import com.chenjiayan.service.MemberService;
import com.chenjiayan.service.ReportService;
import com.chenjiayan.service.SetmealService;
import com.chenjiayan.utils.DateUtils;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.http.protocol.HttpService;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.*;

/**
 * 报表操作
 */
@RestController
@RequestMapping("/report")
public class ReportController {

    @Reference
    private MemberService memberService;

    @Reference
    private SetmealService setmealService;

    @Reference
    private ReportService reportService;
    /**
     * 会员数量折线图数据
     * @return
     */
    @GetMapping("/getMemberReport")
    public Result getMemberReport() throws Exception {
        Map<String,Object> map = new HashMap<>();
        List<String> months = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        // 计算过去一年的12个月
        calendar.add(Calendar.MONTH,-12); // 获取当前时间往前推12个月的时间
        for (int i=0;i<12;i++){
            calendar.add(Calendar.MONTH,1);
            Date date = calendar.getTime();
            months.add(DateUtils.parseDate2String(date, "yyyy.MM"));
        }
        map.put("months",months);
        List<String> months1 = new ArrayList<>();
        Calendar calendar1 = null;
        Date date = null;
        for (int i=11;i>=0;i--){
            calendar1 = Calendar.getInstance();
            calendar1.add(Calendar.MONTH,-i);
            calendar1.add(Calendar.MONTH,1);
            calendar1.set(Calendar.DAY_OF_MONTH,1);
            calendar1.add(Calendar.DAY_OF_MONTH,-1);
            date = calendar1.getTime();
            months1.add(DateUtils.parseDate2String(date, "yyyy.MM.dd"));
        }
        List<Integer> memberCount = memberService.findMemberCountByMonths(months1);
        map.put("memberCount",memberCount);
        return new Result(true, MessageConstant.GET_MEMBER_NUMBER_REPORT_SUCCESS,map);
    }

    /**
     * 套餐预约占比饼形图
     * @return
     */
    @GetMapping("/getSetmealReport")
    public Result getSetmealReport(){
        Map<String,Object> data = new HashMap<>();
        try{
            List<Map<String,Object>> setmealCount = setmealService.findSetmealCount();
            data.put("setmealCount",setmealCount);
            List<String> setmealNames = new ArrayList<>();
            setmealCount.forEach(item->{
                String name = (String) item.get("name");
                setmealNames.add(name);
            });
            data.put("setmealNames",setmealNames);
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,MessageConstant.GET_SETMEAL_COUNT_REPORT_FAIL);
        }
        return new Result(true,MessageConstant.GET_SETMEAL_COUNT_REPORT_SUCCESS,data);
    }

    /**
     * 运营数据统计
     * @return
     */
    @GetMapping("/getBusinessReportData")
    public Result getBusinessReportData(){
        try{
            Map<String,Object> data = reportService.getBusinessReportData();
            return new Result(true,MessageConstant.GET_BUSINESS_REPORT_SUCCESS,data);
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,MessageConstant.GET_BUSINESS_REPORT_FAIL);
        }
    }

    /**
     * 导出运营数据
     * @return
     */
    @GetMapping("/exportBusinessReport")
    public void exportBusinessReport(HttpServletRequest request, HttpServletResponse response){
        try{
            Map<String,Object> result = reportService.getBusinessReportData();
            String reportDate = (String) result.get("reportDate");
            Integer todayNewMember = (Integer) result.get("todayNewMember");
            Integer totalMember = (Integer) result.get("totalMember");
            Integer thisWeekNewMember = (Integer) result.get("thisWeekNewMember");
            Integer thisMonthNewMember = (Integer) result.get("thisMonthNewMember");
            Integer todayOrderNumber = (Integer) result.get("todayOrderNumber");
            Integer thisWeekOrderNumber = (Integer) result.get("thisWeekOrderNumber");
            Integer thisMonthOrderNumber = (Integer) result.get("thisMonthOrderNumber");
            Integer todayVisitsNumber = (Integer) result.get("todayVisitsNumber");
            Integer thisWeekVisitsNumber = (Integer) result.get("thisWeekVisitsNumber");
            Integer thisMonthVisitsNumber = (Integer) result.get("thisMonthVisitsNumber");
            List<Map> hotSetmeal = (List<Map>) result.get("hotSetmeal");

            // 基于提供的Excel模板文件在内存中创建一个Excel表格对象
            String filePath = request.getSession().getServletContext().getRealPath("template") + File.separator + "report_template.xlsx";
            XSSFWorkbook excel = new XSSFWorkbook(new FileInputStream(filePath));
            // 读取第一个工作表
            XSSFSheet sheet = excel.getSheetAt(0);
            // 获得第三行
            XSSFRow row = sheet.getRow(2);
            XSSFCell cell = row.getCell(5);
            cell.setCellValue(reportDate); // 报表日期

            row = sheet.getRow(4);
            cell = row.getCell(5);
            cell.setCellValue(todayNewMember); // 新增会员数

            row = sheet.getRow(5);
            row.getCell(5).setCellValue(thisWeekNewMember);//本周新增会员数
            row.getCell(7).setCellValue(thisMonthNewMember);//本月新增会员数

            row = sheet.getRow(7);
            row.getCell(5).setCellValue(todayOrderNumber);//今日预约数
            row.getCell(7).setCellValue(todayVisitsNumber);//今日到诊数

            row = sheet.getRow(8);
            row.getCell(5).setCellValue(thisWeekOrderNumber);//本周预约数
            row.getCell(7).setCellValue(thisWeekVisitsNumber);//本周到诊数

            row = sheet.getRow(9);
            row.getCell(5).setCellValue(thisMonthOrderNumber);//本月预约数
            row.getCell(7).setCellValue(thisMonthVisitsNumber);//本月到诊数

            int rowNum = 12;
            for(Map map : hotSetmeal){//热门套餐
                String name = (String) map.get("name");
                Long setmeal_count = (Long) map.get("setmeal_count");
                BigDecimal proportion = (BigDecimal) map.get("proportion");
                row = sheet.getRow(rowNum ++);
                row.getCell(4).setCellValue(name);//套餐名称
                row.getCell(5).setCellValue(setmeal_count);//预约数量
                row.getCell(6).setCellValue(proportion.doubleValue());//占比
            }
            // 使用输入流进行表格下载
            ServletOutputStream outputStream = response.getOutputStream();
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("content-Disposition","attachment;filename=report.xlsx"); // 指定以附件的形式进行下载
            excel.write(outputStream);
            outputStream.flush();
            excel.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 导出运营数据到PDF文件并提供客户端下载
     * @return
     */
    @GetMapping("/exportBusinessReport4PDF")
    public void exportBusinessReport4PDF(HttpServletRequest request, HttpServletResponse response){

        try{
            Map<String,Object> result = reportService.getBusinessReportData();
            List<Map> hotSetmeal = (List<Map>) result.get("hotSetmeal");

            // 基于提供的Excel模板文件在内存中创建一个Excel表格对象
            String jrxmlPath = request.getSession().getServletContext().getRealPath("template") + File.separator + "health_business3.jrxml";
            String jasperPath = request.getSession().getServletContext().getRealPath("template") + File.separator + "health_business3.jasper";
            // 编辑模板
            JasperCompileManager.compileReportToFile(jrxmlPath,jasperPath);

            //填充数据---使用JavaBean数据源方式填充
            JasperPrint jasperPrint =
                    JasperFillManager.fillReport(jasperPath,result,
                            new JRBeanCollectionDataSource(hotSetmeal));

            // 使用输入流进行表格下载
            ServletOutputStream out = response.getOutputStream();
            response.setContentType("application/pdf");
            response.setHeader("content-Disposition", "attachment;filename=report.pdf"); // 指定以附件的形式进行下载
            //输出文件
            JasperExportManager.exportReportToPdfStream(jasperPrint,out);

            out.flush();


        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
