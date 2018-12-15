package com.ehear.aiot.cloud.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ehear.aiot.cloud.dao.CustomDao;
import com.ehear.aiot.cloud.dao.OperationDao;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
public class ExcelServlet {

    public static final String FILE_SEPARATOR = System.getProperties().getProperty("file.separator");
    public static final String[] rowname = { "time(Second)", "Back", "Waist", "Arm", "Thigh", "Calf", "Foot", "Instep", "Heat",
            "Vibrate", "Music", "Up", "Down" };

    @RequestMapping(value = "/export2007")
    public void export2007(HttpServletRequest request, HttpServletResponse response) {
        String classPath = this.getClass().getClassLoader().getResource("/").getPath();
        classPath = classPath.substring(1, classPath.indexOf("WEB-INF"));
        String docsPath = classPath + "docs";
        String username = (String) request.getSession().getAttribute("username");
        log.info("");
        Date date = new Date();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String fileName = username + "_" + format.format(date) + ".xlsx";
        String filePath = FILE_SEPARATOR + docsPath + FILE_SEPARATOR + fileName;
        try {
            OutputStream os = new FileOutputStream(filePath);
            XSSFWorkbook wb = new XSSFWorkbook();

            // 首先遍历用户的自定义手法
            List<com.realrelax.alexa.bean.CustomBean> cl = CustomDao.getCustomListByUsername(username);
            for (com.realrelax.alexa.bean.CustomBean cb : cl) {
                XSSFSheet sheet = wb.createSheet(cb.getCustom_name());
                sheet.setColumnWidth(2, 12 * 256);
                sheet.setColumnWidth(3, 7 * 256);
                sheet.setColumnWidth(4, 7 * 256);
                sheet.setColumnWidth(5, 7 * 256);
                sheet.setColumnWidth(6, 7 * 256);
                sheet.setColumnWidth(7, 7 * 256);
                sheet.setColumnWidth(8, 7 * 256);
                sheet.setColumnWidth(9, 7 * 256);
                sheet.setColumnWidth(10, 7 * 256);
                sheet.setColumnWidth(11, 7 * 256);
                sheet.setColumnWidth(12, 7 * 256);
                sheet.setColumnWidth(13, 7 * 256);
                sheet.setColumnWidth(14, 7 * 256);
                // 首行固定
                sheet.createFreezePane(0, 1, 0, 1);

                XSSFRow row = sheet.createRow(0);

                for (int i = 0; i < rowname.length; i++) {
                    // row.setHeight((short) (5 * 256));
                    Cell cell = row.createCell(i + 2);
                    XSSFCellStyle style = wb.createCellStyle();
                    style.setFillPattern(HSSFCellStyle.ALIGN_CENTER);
                    style.setFillBackgroundColor(new HSSFColor.ROSE().getIndex());
                    cell.setCellStyle(style);
                    cell.setCellValue(rowname[i]);
                }

                // 获取该手法下所有步骤
                int customId = CustomDao.getCustomIdByNameAndUser(username, cb.getCustom_name());
                List<com.realrelax.alexa.bean.OperationBean> operationBeanList = OperationDao.getOperationListByCustomId(customId);

                // 首先写入秒数
                int maxSecond = 0;
                for (com.realrelax.alexa.bean.OperationBean oBean : operationBeanList) {
                    int tmp = Integer.parseInt(oBean.getOperationTime().split("-")[1]);
                    maxSecond = maxSecond < tmp ? tmp : maxSecond;
                }
                for (int i = 0; i < maxSecond + 2; i++) {
                    XSSFRow row_content = sheet.createRow(i + 1);
                    row_content.createCell(2).setCellValue(i);
                }
                

                // 写入command

                for (com.realrelax.alexa.bean.OperationBean oBean : operationBeanList) {
                    String des = oBean.getOperationDesc();
                    String start = oBean.getOperationTime().split("-")[0];
                    String end = oBean.getOperationTime().split("-")[1];
                    for (int i = 0; i < rowname.length; i++) {
                        if (des.contains(rowname[i])) {
                            for (int j = Integer.parseInt(start); j < Integer.parseInt(end) + 1; j++) {
                                XSSFCellStyle style_blue = wb.createCellStyle();
                                style_blue.setFillPattern(HSSFCellStyle.ALIGN_FILL);
                                style_blue.setFillBackgroundColor(new HSSFColor.LIGHT_GREEN().getIndex());

                                XSSFCellStyle style_red = wb.createCellStyle();
                                style_red.setFillPattern(HSSFCellStyle.ALIGN_FILL);
                                style_red.setFillBackgroundColor(new HSSFColor.LIGHT_ORANGE().getIndex());

                                XSSFRow row_content;
                                if (null == sheet.getRow(j + 2)) {
                                    row_content = sheet.createRow(j + 1);
                                } else {
                                    row_content = sheet.getRow(j + 1);
                                }

                                if (null == row_content.getCell(i + 2)) {
                                    if (des.contains("reverse")) {
                                        row_content.createCell(i + 2).setCellStyle(style_red);
                                    } else {
                                        row_content.createCell(i + 2).setCellStyle(style_blue);
                                    }

                                } else {
                                    if (des.contains("reverse")) {
                                        row_content.getCell(i + 2).setCellStyle(style_red);
                                    } else {
                                        row_content.getCell(i + 2).setCellStyle(style_blue);
                                    }
                                }

                                if (j == Integer.parseInt(start)) {
                                    row_content.getCell(i + 2).setCellValue(des.split(";")[2]);
                                }

                            }
                        }
                    }
                }

            }
            // 写文件
            wb.write(os);
            // 关闭输出流
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        download(filePath, response);
    }

    private void download(String path, HttpServletResponse response) {
        try {
            // path是指欲下载的文件的路径。
            File file = new File(path);
            // 取得文件名。
            String filename = file.getName();
            // 以流的形式下载文件。
            InputStream fis = new BufferedInputStream(new FileInputStream(path));
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            // 清空response
            response.reset();
            // 设置response的Header
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(filename.getBytes(), "ISO-8859-1"));
            response.addHeader("Content-Length", "" + file.length());
            OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/vnd.ms-excel;charset=gb2312");
            toClient.write(buffer);
            toClient.flush();
            toClient.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @RequestMapping(value = "/uploadServlet")
    public void read(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 首先将文件上传到本地服务器
        boolean isMultipart;
        int maxFileSize = 500 * 1024;
        int maxMemSize = 100 * 1024;
        File file;
        String targetPath = null;
        String classPath = this.getClass().getClassLoader().getResource("/").getPath();
        classPath = classPath.substring(1, classPath.indexOf("WEB-INF"));
        String docsPath = classPath + "docs";
        String filePathfinal = FILE_SEPARATOR + docsPath + FILE_SEPARATOR;
        String username = (String) request.getSession().getAttribute("username");
        isMultipart = ServletFileUpload.isMultipartContent(request);
        response.setContentType("text/html;charset=gb2312");
        response.setCharacterEncoding("utf-8");
        java.io.PrintWriter out = response.getWriter();
        if (!isMultipart) {
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet upload</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<p>No file uploaded</p>");
            out.println("</body>");
            out.println("</html>");
            return;
        }
        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setSizeThreshold(maxMemSize);
        System.out.println("(((((((((((((((((((((((((((((((((" + filePathfinal);
        factory.setRepository(new File(filePathfinal));
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setSizeMax(maxFileSize);
        try {
            List<?> fileItems = upload.parseRequest(request);
            Iterator<?> i = fileItems.iterator();
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet upload</title>");
            out.println("</head>");
            out.println("<body>");
            while (i.hasNext()) {
                FileItem fi = (FileItem) i.next();

                if (!fi.isFormField()) {
                    String fileName = fi.getName();
                    if (fileName.lastIndexOf("\\") >= 0) {
                        file = new File(targetPath + fileName.substring(fileName.lastIndexOf("\\")));
                    } else {
                        file = new File(targetPath + fileName.substring(fileName.lastIndexOf("\\") + 1));
                    }
                    fi.write(file);
                    int resultnum = readExcel(file, username);
                    file.delete();
                    request.setAttribute("list", resultnum);
                    RequestDispatcher dispatcher = request.getRequestDispatcher("/read.jsp");
                    dispatcher.forward(request, response);
                } else {
                    String fieldName = fi.getFieldName();
                    if (fieldName.equals("user")) {
                        String user = fi.getString();
                        out.println(fieldName + ":" + user + "<br>");
                        targetPath = filePathfinal;
                    }

                }

            }
            out.println("</body>");
            out.println("</html>");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    /**
     * 对外提供读取excel 的方法
     * 
     * @param mac
     */
    public int readExcel(File file, String username) throws IOException {
        String fileName = file.getName();
        String extension = fileName.lastIndexOf(".") == -1 ? "" : fileName.substring(fileName.lastIndexOf(".") + 1);
        if ("xls".equals(extension)) {
            return readOperation(file, username);
        } else if ("xlsx".equals(extension)) {
            return readOperation(file, username);
        } else {
            throw new IOException("不支持的文件类型");
        }
    }

    /**
     * 读取 office 2003 excel
     * 
     * @throws IOException
     * @throws FileNotFoundException
     */
    @SuppressWarnings("unused")
    private List<List<Object>> read2003Excel(File file) throws IOException {
        List<List<Object>> list = new LinkedList<List<Object>>();
        HSSFWorkbook hwb = new HSSFWorkbook(new FileInputStream(file));
        HSSFSheet sheet = hwb.getSheetAt(0);
        Object value = null;
        HSSFRow row = null;
        HSSFCell cell = null;
        int counter = 0;
        for (int i = sheet.getFirstRowNum(); counter < sheet.getPhysicalNumberOfRows(); i++) {
            row = sheet.getRow(i);
            if (row == null) {
                continue;
            } else {
                counter++;
            }
            List<Object> linked = new LinkedList<Object>();
            for (int j = row.getFirstCellNum(); j <= row.getLastCellNum(); j++) {
                cell = row.getCell(j);
                if (cell == null) {
                    continue;
                }
                DecimalFormat df = new DecimalFormat("0");// 格式化 number String
                                                          // 字符
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 格式化日期字符串
                DecimalFormat nf = new DecimalFormat("0.00");// 格式化数字
                switch (cell.getCellType()) {
                case XSSFCell.CELL_TYPE_STRING:
                    value = cell.getStringCellValue();
                    System.out.println(i + "行" + j + " 列 is String type" + "\tValue:" + value);
                    break;
                case XSSFCell.CELL_TYPE_NUMERIC:
                    if ("@".equals(cell.getCellStyle().getDataFormatString())) {
                        value = df.format(cell.getNumericCellValue());
                    } else if ("General".equals(cell.getCellStyle().getDataFormatString())) {
                        value = nf.format(cell.getNumericCellValue());
                    } else {
                        value = sdf.format(HSSFDateUtil.getJavaDate(cell.getNumericCellValue()));
                    }
                    System.out.println(i + "行" + j + " 列 is Number type ; DateFormt:" + cell.getCellStyle().getDataFormatString()
                            + "\tValue:" + value);
                    break;
                case XSSFCell.CELL_TYPE_BOOLEAN:
                    value = cell.getBooleanCellValue();
                    System.out.println(i + "行" + j + " 列 is Boolean type" + "\tValue:" + value);
                    break;
                case XSSFCell.CELL_TYPE_BLANK:
                    value = "";
                    System.out.println(i + "行" + j + " 列 is Blank type" + "\tValue:" + value);
                    break;
                default:
                    value = cell.toString();
                    System.out.println(i + "行" + j + " 列 is default type" + "\tValue:" + value);
                }
                if (value == null || "".equals(value)) {
                    continue;
                }
                linked.add(value);
            }
            list.add(linked);
        }
        return list;
    }

    /**
     * 读取Office 2007 excel
     */
    @SuppressWarnings("unused")
    private List<List<Object>> read2007Excel(File file) throws IOException {
        List<List<Object>> list = new LinkedList<List<Object>>();
        // 构造 XSSFWorkbook 对象，strPath 传入文件路径
        XSSFWorkbook xwb = new XSSFWorkbook(new FileInputStream(file));
        // 读取第一章表格内容
        XSSFSheet sheet = xwb.getSheetAt(0);
        Object value = null;
        XSSFRow row = null;
        XSSFCell cell = null;
        int counter = 0;
        for (int i = sheet.getFirstRowNum(); counter < sheet.getPhysicalNumberOfRows(); i++) {
            row = sheet.getRow(i);
            if (row == null) {
                continue;
            } else {
                counter++;
            }
            List<Object> linked = new LinkedList<Object>();
            for (int j = row.getFirstCellNum(); j <= row.getLastCellNum(); j++) {
                cell = row.getCell(j);
                if (cell == null) {
                    continue;
                }
                DecimalFormat df = new DecimalFormat("0");// 格式化 number String
                                                          // 字符
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 格式化日期字符串
                DecimalFormat nf = new DecimalFormat("0.00");// 格式化数字
                switch (cell.getCellType()) {
                case XSSFCell.CELL_TYPE_STRING:
                    System.out.println(i + "行" + j + " 列 is String type");
                    value = cell.getStringCellValue();
                    break;
                case XSSFCell.CELL_TYPE_NUMERIC:
                    System.out
                            .println(i + "行" + j + " 列 is Number type ; DateFormt:" + cell.getCellStyle().getDataFormatString());
                    if ("@".equals(cell.getCellStyle().getDataFormatString())) {
                        value = df.format(cell.getNumericCellValue());
                    } else if ("General".equals(cell.getCellStyle().getDataFormatString())) {
                        value = nf.format(cell.getNumericCellValue());
                    } else {
                        value = sdf.format(HSSFDateUtil.getJavaDate(cell.getNumericCellValue()));
                    }
                    break;
                case XSSFCell.CELL_TYPE_BOOLEAN:
                    System.out.println(i + "行" + j + " 列 is Boolean type");
                    value = cell.getBooleanCellValue();
                    break;
                case XSSFCell.CELL_TYPE_BLANK:
                    System.out.println(i + "行" + j + " 列 is Blank type");
                    value = "";
                    break;
                default:
                    System.out.println(i + "行" + j + " 列 is default type");
                    value = cell.toString();
                }
                if (value == null || "".equals(value)) {
                    continue;
                }
                linked.add(value);
            }
            list.add(linked);
        }
        return list;
    }

    // 从excel中读取OperationBeanList
    private int readOperation(File file, String username) throws IOException {
        int op_num = 0;
        XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(file));
        XSSFSheet sheet = null;
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {// 获取每个Sheet表
            sheet = workbook.getSheetAt(i);
            // 首先查数据库是否存在该自定义模式
            if (CustomDao.getCustomIdByNameAndUser(username, sheet.getSheetName()) != 0) {
                // 删除原有的custom
                CustomDao.delCustom(CustomDao.getCustomIdByNameAndUser(username, sheet.getSheetName()));
            }

            // 创建新的custom
            com.realrelax.alexa.bean.CustomBean cBean = new com.realrelax.alexa.bean.CustomBean();
            cBean.setCustom_name(sheet.getSheetName());
            cBean.setCustom_user_name(username);
            cBean.setCustom_create_time(new Date());
            CustomDao.addCustom(cBean);

            // 定义二维数组存储颜色信息
            int[][] arr = new int[sheet.getLastRowNum() + 1][rowname.length];
            for (int j = 0; j < sheet.getLastRowNum() + 1; j++) {// getLastRowNum，获取最后一行的行标
                XSSFRow row = sheet.getRow(j);
                if (row != null) {
                    for (int k = 3; k < rowname.length + 3; k++) {// getLastCellNum，是获取最后一个不为空的列是第几个
                        if (row.getCell(k) != null) { // getCell 获取单元格数据
                            // 获取单元格颜色信息,42代表正转，52代表反转
                            if (42 == row.getCell(k).getCellStyle().getFillBackgroundColor()) {
                                arr[j][k - 3] = 42;
                            } else if (52 == row.getCell(k).getCellStyle().getFillBackgroundColor()) {
                                arr[j][k - 3] = 52;
                            }
                            // System.out.print(row.getCell(k).getCellStyle().getFillBackgroundColor()
                            // + "\t");
                        } else {
                            // System.out.print("\t");
                        }
                    }
                }
                // System.out.println(""); // 读完一行后换行
            }

            // 处理二维数组
            int[][] resultArr = convertArr(arr);
            for (int j = 0; j < rowname.length; j++) {
                for (int k = 0; k < resultArr[j].length; k++) {
                    // 正转
                    try {
                        if (null != sheet.getRow(k).getCell(j + 3)) {
                            sheet.getRow(k).getCell(j + 3).setCellType(Cell.CELL_TYPE_STRING);
                        }

                    } catch (Exception e) {
                        // TODO: handle exception
                    }

                    if ((resultArr[j][k] == 42
                            && (!sheet.getRow(k).getCell(j + 3).getRichStringCellValue().getString().equals("")))
                            || (resultArr[j][k] == 42 && resultArr[j][k - 1] != 42)) {
                        String speed = sheet.getRow(k).getCell(j + 3).getRichStringCellValue().getString().replace(" ", "");
                        int start_time = k - 1;
                        // 计算结束时间
                        int count = 0;
                        for (int m = k; m < resultArr[j].length; m++) {
                            if (resultArr[j][m] != 42) {
                                break;
                            } else {
                                count++;
                            }
                        }
                        int end_time = k + count - 2;
                        com.realrelax.alexa.bean.OperationBean ob = new com.realrelax.alexa.bean.OperationBean();
                        if(speed.equals(""))
                        {
                            speed = "60";
                        }
                        ob.setOperationDesc(rowname[j + 1] + ";" + "forward" + ";" + speed + ";" + "start");
                        ob.setOperationTime(start_time + "-" + end_time);
                        ob.setCustomId(CustomDao.getCustomIdByNameAndUser(username, sheet.getSheetName()));
                        OperationDao.addOperation(ob);
                        op_num++;
                    } else if ((resultArr[j][k] == 52
                            && !sheet.getRow(k).getCell(j + 3).getRichStringCellValue().getString().equals(""))
                            || (resultArr[j][k] == 52 && resultArr[j][k - 1] != 52)) {
                        // 反转
                        String speed = sheet.getRow(k).getCell(j + 3).getRichStringCellValue().getString().replace(" ", "");
                        int start_time = k - 1;
                        // 计算结束时间
                        int count = 0;
                        for (int m = k; m < resultArr[j].length; m++) {
                            if (resultArr[j][m] != 52) {
                                break;
                            } else {
                                count++;
                            }
                        }
                        int end_time = k + count - 2;
                        com.realrelax.alexa.bean.OperationBean ob = new com.realrelax.alexa.bean.OperationBean();
                        if(speed.equals(""))
                        {
                            speed = "60";
                        }
                        ob.setOperationDesc(rowname[j + 1] + ";" + "reverse" + ";" + speed + ";" + "start");
                        ob.setOperationTime(start_time + "-" + end_time);
                        ob.setCustomId(CustomDao.getCustomIdByNameAndUser(username, sheet.getSheetName()));
                        OperationDao.addOperation(ob);
                        op_num++;
                    }
                }

            }

            log.info("读取sheet表：" + workbook.getSheetName(i) + " 完成");
        }
        return op_num;
    }

    public int[][] convertArr(int[][] arr) {
        int a[][] = arr;
        int b[][] = new int[arr[0].length][arr.length];
        for (int i = 0; i < b.length; i++) {
            for (int j = 0; j < b[i].length; j++) {
                b[i][j] = a[j][i];
            }
        }
        for (int i = 0; i < b.length; i++) {
            for (int j = 0; j < b[i].length; j++) {
                // System.out.print(b[i][j] + " ");
            }
            // System.out.println();
        }
        return b;
    }

}
