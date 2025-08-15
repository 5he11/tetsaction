package com.frenzy.core.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import com.alibaba.fastjson.JSON;
import com.frenzy.core.constant.HttpStatus;
import com.frenzy.core.exception.ServiceException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class yftools {
//	@Autowired
//	private static ApplicationContext applicationContext;

//	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	public static Logger logger = LoggerFactory.getLogger(yftools.class);
//	public static Logger logger = LoggerFactory.getLogger(applicationContext.getClass());

	/**
	 * 	// 求和
	 * 	int sum = list.stream().mapToInt(Pool::getValue).sum();
	 * 	// 最大值
	 * 	OptionalInt max = list.stream().mapToInt(Pool::getValue).max();
	 * 	// 最小值
	 * 	OptionalInt min = list.stream().mapToInt(Pool::getValue).min();
	 * 	// 平均值
	 * 	OptionalDouble average = list.stream().mapToInt(Pool::getValue).average();
	 * 	List<Integer> idList  = list.stream().map(o -> o.getId()).collect(Collectors.toList());
	 *
	 * 	// 使用 Stream 计算属性的合计值
	 * BigDecimal sum = res.stream()
	 *         .map(PresaleybpaymonthsummarysReportResponse::getCollection) // 获取每个对象的 BigDecimal 属性值
	 *         .filter(Objects::nonNull) // 过滤掉为 null 的值
	 *         .reduce(BigDecimal.ZERO, BigDecimal::add); // 将所有值累加起来
	 */

	public static String encryptPassword(String code){
		Digester md5 = new Digester(DigestAlgorithm.SHA256);
		String encode = md5.digestHex(code);
		return encode;
	}

	public static String listToString1(List<String> list) {
		StringBuilder sb = new StringBuilder();
		if (CollectionUtil.isNotEmpty(list)) {
			for (int i = 0; i < list.size(); i++) {
				if (i < list.size() - 1) {
					sb.append(list.get(i) + ",");
				} else {
					sb.append(list.get(i));
				}
			}
		}
		return sb.toString();
	}



	//"嗯我一下哦哦哦谢谢哦哦哦谢谢@#[@八神童子]|[10]#@ ,凑个人数@#[@八神童子]|[10]#@ 帖子的评论"
	public static String processTextWithRegex(String str) {
		String res = str;
		String regex = "@#\\[([^#|]+)\\]\\|([^#|]+)#@";// 定义正则表达式
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);

		while (matcher.find()) {
			final String match = matcher.group(0);  // 完整匹配文本
			final String replacement = matcher.group(1);  // 第一个捕获组文本
			res = str.replace(match,replacement);
		}

		return res;
	}


	public static int[] getWidthAndHeight(String url) {
		try {
			int[] wh=new int[2];
			InputStream is = new URL(url).openStream();
			BufferedImage sourceImg = ImageIO.read(is);
			if (sourceImg!=null){
				int width = sourceImg.getWidth();
				int height = sourceImg.getHeight();
				wh[0]=width;
				wh[1]=height;
				return wh;
			}
		} catch (IOException e) {
			logger.error("获取图片宽高失败");
			e.printStackTrace();
		}
		return null;
	}

	public static String getExtensionName(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot >-1) && (dot < (filename.length() - 1))) {
				return "."+filename.substring(dot + 1);
			}
		}
		return "."+filename;
	}




	public static void chkNullUserException(Object pram){
		if (pram==null){
			throw new ServiceException("用户信息不存在", HttpStatus.UNAUTHORIZED);
		}
	}

	public static void chkEmptyUserException(String pram){
		if (yftools.isEmpty(pram)){
			throw new ServiceException("用户信息不存在", HttpStatus.UNAUTHORIZED);
		}
	}


	public static void chkNullTeamException(Object pram){
		if (pram==null){
			throw new ServiceException("团队不存在或团队已过期", HttpStatus.TEAM_ERR);
		}
	}
	public static void chkEmptyTeamException(String pram){
		if (yftools.isEmpty(pram)){
			throw new ServiceException("团队不存在或团队已过期", HttpStatus.TEAM_ERR);
		}
	}
	public static void throwTeamException(){
		throw new ServiceException("团队不存在或团队已过期", HttpStatus.TEAM_ERR);
	}





	public static void chkNullProjectException(Object pram){
		if (pram==null){
			throw new ServiceException("项目不存在或没有项目权限", HttpStatus.PROJECT_ERR);
		}
	}
	public static void chkEmptyProjectException(String pram){
		if (yftools.isEmpty(pram)){
			throw new ServiceException("项目不存在或没有项目权限", HttpStatus.PROJECT_ERR);
		}
	}
	public static void throwProjectException(){
		throw new ServiceException("项目不存在或没有项目权限", HttpStatus.PROJECT_ERR);
	}







	public static void chkNullTenantException(Object pram){
		if (pram==null){
			throw new ServiceException("标识信息不存在", HttpStatus.TENANT_NOT_EXIST);
		}
	}

	public static void chkEmptyTenantException(String pram){
		if (yftools.isEmpty(pram)){
			throw new ServiceException("标识信息不存在", HttpStatus.TENANT_NOT_EXIST);
		}
	}


	public static void chkExpireTenantException(){
		throw new ServiceException("租户已过期", HttpStatus.TENANT_EXPIRE);
	}



	public static void chkNullException(Object pram,String tips){
		if (pram==null){
			throw new ServiceException(tips);
		}
	}

	public static void chkEmptyException(String pram,String tips){
		if (yftools.isEmpty(pram)){
			throw new ServiceException(tips);
		}
	}
	public static void throwException(String tips){
			throw new ServiceException(tips);
	}

	public static void throwBussinessException(String tips,String detailMessage){
		throw new ServiceException(detailMessage, HttpStatus.BUSSINESS_ERROR_CODE);
	}

	public static boolean isEquals(String a,String b) {
		try {
			if (a==null)
				return false;
			else if (b==null)
				return false;
			else if (a.equals(b))
				return true;
			else
				return false;
		} catch (Exception e) {
			return true;
		}
	}

	public static boolean isContans(String checkstr,String str) {
		if(str.indexOf(checkstr)!=-1){
			System.out.println("包含");
			return true;
		}
		return false;
	}


	public static boolean isContansList(String checkstr,String str) {
		if (isEmpty(checkstr))
			return false;
		List<String> result = Arrays.asList(checkstr.split(","));
		if (result.contains(str))
			return true;
		return false;
	}

	// List 转字符串
	public static String listToString(List list, String separator) {
		return StringUtils.join(list.toArray(), separator);
	}

	// 字符串 转 List
	public static List<String> stringToList(String str, String separator) {
		List<String> result = new ArrayList<>(Arrays.asList(str.split(separator)));

		return result;
	}


	public static String filterEmoji(String source) {
		if (source != null && source.length() > 0) {
			return source.replaceAll("[\ud800\udc00-\udbff\udfff\ud800-\udfff]", "");
		} else {
			return source;
		}
	}

	/**

	 * @param s 需要判断字符串的长度，中文2个字符，数字字符1个字符；
	 * @return 字符长度
	 */
	public static int getlength(String s){
		if(s==null){
			return 0;
		}
		char[] c=s.toCharArray();
		int len =0;
		for (int i = 0; i < c.length; i++) {
			len++;
			if (!isLetter(c[i])) {
				len++;
			}
		}
		return len;
	}
	public static boolean isLetter(char c){
		int k=0x80;
		return c/k==0?true:false;
	}


	public static boolean checkUserName(String userName) {
//		String regex = "([a-z]|[A-Z]|[0-9]|[\\u4e00-\\u9fa5])+";
		String regex = "([~!@#$&*()?!._\\-()+]|[a-z]|[A-Z]|[0-9]|[\\u4e00-\\u9fa5])+";
//		String regex = "^(?!_)(?!.*?_$)[a-zA-Z0-9_\\u4e00-\\u9fa5]+$";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(userName);
		return m.matches();
	}


	public static String userlevel(Double score) {
		if (score<1000){
			return "3";
		}else if (score>=1000 && score<2000){
			return "4";
		}else if (score>=2000 && score<4000){
			return "5";
		}else if (score>=4000 && score<7000){
			return "6";
		}else if (score>=7000 && score<12000){
			return "186";
		}else if (score>=12000 && score<20000){
			return "187";
		}else if (score>=20000){
			return "188";
		}
		return "3";
	}
	
	

	public static String getJsonStr(Object obj){
		return JSON.toJSONString(obj);
	}

	public static boolean isImage(String fileName) { //length表示生成字符串的长度

		if("jpg".equals(fileName.substring(fileName.length() - 3))){
			return true;
		}else if("png".equals(fileName.substring(fileName.length() - 3))){
			return true;
		}else if("gif".equals(fileName.substring(fileName.length() - 3))){
			return true;
		}else if("bmp".equals(fileName.substring(fileName.length() - 3))){
			return true;
		}else if("jpeg".equals(fileName.substring(fileName.length() - 4))){
			return true;
		}else{
			return false;
		}



	}

	public static String getRandomString(int length) { //length表示生成字符串的长度
		String base = "abcdefghijklmnopqrstuvwxyz";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString();
	}

	public static String getRandomStringUP(int length) { //length表示生成字符串的长度
		String base = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString();
	}

	public static int BtoInt(String b) {
		if("true".equals(b)){
			return 1;
		}else{
			return 0;
		}

	}


	public static String[] fv(String arr){
		String[] F_V=arr.split("\\|");
		if(F_V.length==2){
//			O_Value=F_V[0];
//			O_Text=F_V[1];
			return F_V;
		}else{
			String[] F_V2=new String[]{F_V[0],F_V[0]};
//			O_Value=F_V[0];
//			F_V[1]=F_V[0];
			return F_V2;
		}

	}




	public static long getUnixTime(){
		return System.currentTimeMillis() / 1000;
	}

	/**
	 * 随机生成六位数验证码
	 * @return
	 */
	public static int getRandomNum(){
		Random r = new Random();
		return r.nextInt(900000)+100000;//(Math.random()*(999999-100000)+100000)
	}

	public static boolean chkcontain(String powerlist, String menuid) {
		return powerlist.contains(menuid);
	}
	public static boolean chkpower(String powerlist, String menuid) {
		try{
			String[] power=str2StrArray(powerlist);
			for (String s : power) {
				if (s.equals(menuid)) {
					return true;
				}
			}
		}catch (Exception e){

		}

		return false;
	}

	//获取当前时间戳
	public static Timestamp now(){
	Date datetime=new Date();
    Timestamp nowtime=new Timestamp(datetime.getTime());
	return nowtime;
	}

	public static String nowStr(){
		String formattedDateTime = LocalDateTime.now()
				.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		return formattedDateTime;
	}

	public static String nowStrCn(){
		// 获取UTC时间
		Instant now = Instant.now();

		// 转换为北京时间
		ZonedDateTime beijingTime = now.atZone(ZoneId.of("Asia/Shanghai"));

		// 格式化输出
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String formattedTime = beijingTime.format(formatter);

//		System.out.println("当前北京时间: " + formattedTime);
		return formattedTime;
	}
	/*
     * 将时间转换为时间戳
     */
	public static String dateToStamp(String s){
		try {
			String res;
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = simpleDateFormat.parse(s);
			long ts = date.getTime();
			res = String.valueOf(ts);
			return res;
		}catch (Exception e){
			return "";
		}

	}






	//HttpServletRequest request = this.getRequest();
	public String getIP(HttpServletRequest request) throws Exception {
		String ip = "";
		if (request.getHeader("x-forwarded-for") == null) {
			ip = request.getRemoteAddr();
		}else{
			ip = request.getHeader("x-forwarded-for");
		}
		return ip;
	}

	/*
	public String getTimeByDate(){
        Date date = new Date();
        DateFormat df1 = DateFormat.getDateInstance();//日期格式，精确到日
        System.out.println(df1.format(date));
        DateFormat df2 = DateFormat.getDateTimeInstance();//可以精确到时分秒
        System.out.println(df2.format(date));
        DateFormat df3 = DateFormat.getTimeInstance();//只显示出时分秒
        System.out.println(df3.format(date));
        DateFormat df4 = DateFormat.getDateTimeInstance(DateFormat.FULL,DateFormat.FULL); //显示日期，周，上下午，时间（精确到秒）
        System.out.println(df4.format(date)); 
        DateFormat df5 = DateFormat.getDateTimeInstance(DateFormat.LONG,DateFormat.LONG); //显示日期,上下午，时间（精确到秒）
        System.out.println(df5.format(date));
        DateFormat df6 = DateFormat.getDateTimeInstance(DateFormat.SHORT,DateFormat.SHORT); //显示日期，上下午,时间（精确到分）
        System.out.println(df6.format(date));
        DateFormat df7 = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.MEDIUM); //显示日期，时间（精确到分）
        System.out.println(df7.format(date));
        return df1.format(date);
    }*/
    public String getTimeByCalendar(){
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);//获取年份
        int month=cal.get(Calendar.MONTH)+1;//获取月份
        int day=cal.get(Calendar.DATE);//获取日
        int hour=cal.get(Calendar.HOUR);//小时
        int minute=cal.get(Calendar.MINUTE);//分           
        int second=cal.get(Calendar.SECOND);//秒
        int WeekOfYear = cal.get(Calendar.DAY_OF_WEEK);//一周的第几天
        System.out.println("现在的时间是：公元"+year+"年"+month+"月"+day+"日      "+hour+"时"+minute+"分"+second+"秒       星期"+WeekOfYear);
        return year+"-"+month+"-"+day+" "+hour+":"+minute+":"+second;
    }

	public static int getYear(){
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);//获取年份
		return year;
	}
	public static int getMonth(){
		Calendar cal = Calendar.getInstance();
		int month=cal.get(Calendar.MONTH)+1;//获取月份
		return month;
	}
	public static int getDay(){
		Calendar cal = Calendar.getInstance();
		int day=cal.get(Calendar.DATE);//获取日
		return day;
	}

    public String getTime(){
        Calendar cal = Calendar.getInstance();
        int hour=cal.get(Calendar.HOUR);//小时
        int minute=cal.get(Calendar.MINUTE);//分           
        int second=cal.get(Calendar.SECOND);//秒
        return hour+":"+minute+":"+second;
    }
    public static String getDate(){
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);//获取年份
        int month=cal.get(Calendar.MONTH)+1;//获取月份
        int day=cal.get(Calendar.DATE);//获取日
        return year+"-"+month+"-"+day;
    }
    
    public String getWeek(String day) throws ParseException{
    	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date dateTwo = format.parse(day);
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateTwo);
        int WeekOfYear = cal.get(Calendar.DAY_OF_WEEK);//一周的第几天
        switch (WeekOfYear) {
		case 1:
			return "7";
		case 2:
			return "1";
		case 3:
			return "2";
		case 4:
			return "3";
		case 5:
			return "4";
		case 6:
			return "5";
		case 7:
			return "6";
		default:
			return null;
        }

    }
    public String getWeekHZ(String day) throws ParseException{
    	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date dateTwo = format.parse(day);
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateTwo);
        int WeekOfYear = cal.get(Calendar.DAY_OF_WEEK);//一周的第几天
        switch (WeekOfYear) {
		case 1:
			return "日";
		case 2:
			return "一";
		case 3:
			return "二";
		case 4:
			return "三";
		case 5:
			return "四";
		case 6:
			return "五";
		case 7:
			return "六";
		default:
			return null;
        }

        
    }
    public static String getOrder(String pre) {
    	Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);//获取年份
        int month=cal.get(Calendar.MONTH)+1;//获取月份
        int day=cal.get(Calendar.DATE);//获取日
        int hour=cal.get(Calendar.HOUR);//小时
        int minute=cal.get(Calendar.MINUTE);//分           
        int second=cal.get(Calendar.SECOND);//秒
        String orderstr=pre;
        orderstr +=year;
        orderstr +=month;
        orderstr +=day;
        orderstr +=hour;
        orderstr +=minute;
        orderstr +=second;
        return orderstr +=getRandNum(4);
    }
	public static String getUserOrder(String pre,String uid) {
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);//获取年份
		int month=cal.get(Calendar.MONTH)+1;//获取月份
		int day=cal.get(Calendar.DATE);//获取日
		int hour=cal.get(Calendar.HOUR);//小时
		int minute=cal.get(Calendar.MINUTE);//分
		int second=cal.get(Calendar.SECOND);//秒
		String orderstr=pre;
		orderstr +=year;
		orderstr +=month;
		orderstr +=day;
		orderstr +=hour;
		orderstr +=minute;
		orderstr +=second;
		orderstr +=uid;
		return orderstr +=getRandNum(4);
	}

	public static String getoutOrder(String pre) {
		Calendar cal = Calendar.getInstance();
		int day=cal.get(Calendar.DATE);//获取日
		int hour=cal.get(Calendar.HOUR);//小时
		int minute=cal.get(Calendar.MINUTE);//分
		int second=cal.get(Calendar.SECOND);//秒
		String orderstr=pre;
		orderstr +=day;
		orderstr +=hour;
		orderstr +=minute;
		orderstr +=second;
		return orderstr +=getRandNum(4);
	}


	public static String getOrderIdByUUId() {
		int machineId = 1;//最大支持1-9个集群机器部署
		int hashCodeV = UUID.randomUUID().toString().hashCode();
		if(hashCodeV < 0) {//有可能是负数
			hashCodeV = - hashCodeV;
		}
		// 0 代表前面补充0
		// 4 代表长度为4
		// d 代表参数为正数型
		return machineId + String.format("%015d", hashCodeV);
	}


    public static String getRandNum(int length) {
    	String orderstr="";
    orderstr += (int)(Math.random()*9+1);

    for(int i = 0; i < length; i++){

    orderstr += (int)(Math.random()*10);
    }
	return orderstr;
    }
    
    
	public static String getAge(String birthday) throws ParseException {
		String nianling;
		Date nowdate = new Date();
//		java.sql.Date showdate = new java.sql.Date(nowdate.getYear(), nowdate.getMonth(), nowdate.getDate());
		if ("".equals(birthday)) {
			nianling = "未知年龄";
		} else {

			// Date dateTwo=java.sql.Date.valueOf(birthday);

			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Date dateTwo = format.parse(birthday);

			int reVal = 0;
			int numOne = 0;
			int numTwo = 0;
			Calendar calOne = Calendar.getInstance();
			Calendar calTwo = Calendar.getInstance();
			calOne.setTime(nowdate);
			calTwo.setTime(dateTwo);
			numTwo = calTwo.get(1);
			numOne = calOne.get(1);
			reVal = numOne - numTwo;
			nianling = reVal + "";
		}
		return nianling;
	}

	public String getJuli(String lat, String lng, String readlat, String readlng) {
		// ------------------------------------开始计算距离------------------
		// 入口lng,lat
		// 入口readlng,readlat
		// 出口String juli
		if (!"".equals(lng) && !"".equals(lat) && lng != null && lat != null
				&& lng.substring(0, 1).matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$")
				&& lat.substring(0, 1).matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$") && !"".equals(readlng)
				&& !"".equals(readlat) && readlng != null && readlat != null
				&& readlng.substring(0, 1).matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$")
				&& readlat.substring(0, 1).matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$")) {

			Double lat1 = Double.parseDouble(lat);
			Double lng1 = Double.parseDouble(lng);
			Double lat2 = Double.parseDouble(readlat);
			Double lng2 = Double.parseDouble(readlng);
			String juli = "0";
			double EARTH_RADIUS = 6378.137;
			double radLat1 = lat1 * Math.PI / 180.0;
			double radLat2 = lat2 * Math.PI / 180.0;
			double difference = radLat1 - radLat2;
			double mdifference = lng1 * Math.PI / 180.0 - lng2 * Math.PI / 180.0;
			double distance = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(difference / 2), 2)
					+ Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(mdifference / 2), 2)));
			distance = distance * EARTH_RADIUS;
			//distance = Math.round(distance * 10000) / 10000;
			juli = distance + "";
			return juli = juli.substring(0, juli.indexOf(".")+2);
		} else {
			return "0";

		}
	}




	/**
	 * 验证邮箱
	 * @param email
	 * @return
	 */
	public static boolean checkEmail(String email){
		boolean flag = false;
		try{
			String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
			Pattern regex = Pattern.compile(check);
			Matcher matcher = regex.matcher(email);
			flag = matcher.matches();
		}catch(Exception e){
			flag = false;
		}
		return flag;
	}

	/**
	 * 验证手机号码
	 * @param mobileNumber
	 * @return
	 */
	public static boolean checkMobileNumber(String mobileNumber){
		boolean flag = false;
		try{
			Pattern regex = Pattern.compile("^(((13[0-9])|(14[0-9])|(15[0-9])|(16[0-9])|(17[0-9])|(18[0-9])|(19[0-9]))\\d{8})|(0\\d{2}-\\d{8})|(0\\d{3}-\\d{7})$");
			Matcher matcher = regex.matcher(mobileNumber);
			flag = matcher.matches();
		}catch(Exception e){
			flag = false;
		}
		return flag;
	}

	/**
	 * 打印调试信息
	 * 
	 * @return
	 */
	public void log(String Value) {
		System.out.println(Value); // 在控制台中输入异常信息
	}

	/**
	 * 判断是否为数字,是的话格式化返回，不是返回0
	 * 
	 * @return
	 */
	public static int chkclng(String ID) {
		try {
			Integer.parseInt(ID);
			return Integer.parseInt(ID);
		} catch (NumberFormatException e) {
			return 0;
		}
	}
	public static int chkintmoney(String ID) {

		try {
			if(chkclng(ID)==0){
				if(ID.contains(".")){
					return chkclng(ID.substring(0,ID.indexOf(".")));
				}
			}
			return Integer.parseInt(ID.trim());
		} catch (NumberFormatException e) {
			System.out.println(e.getMessage());
			return 0;
		}
	}

	public static double toDouble(double d,int n) {
		// 旧方法，已经不再推荐使用
//        BigDecimal bg = new BigDecimal(d).setScale(2, BigDecimal.ROUND_HALF_UP);


		// 新方法，如果不需要四舍五入，可以使用RoundingMode.DOWN
		BigDecimal bg = new BigDecimal(d).setScale(n, RoundingMode.UP);


		return bg.doubleValue();
	}

	public static Double chkdouble(String ID) {

		try {
			//Double.valueOf(ID.toString());
			return Double.valueOf(ID.toString());
		} catch (NumberFormatException e) {
			System.out.println(e.getMessage());
			return 0.0;
		}
	}

	/**
	 * 判断是否为null，是的话返回空
	 * 
	 * @return
	 */
	public static String chknull(String value) {
		if (value == null || "".equals(value)) {
			return value = "";
		} else {
			return value;
		}
	}



	/**
	 * 检测字符串是否不为空(null,"","null")
	 * @param s
	 * @return 不为空则返回true，否则返回false
	 */
	public static boolean notEmpty(String s){
		return s!=null && !"".equals(s) && !"null".equals(s);
	}

	/**
	 * 检测字符串是否为空(null,"","null")
	 * @param s
	 * @return 为空则返回true，不否则返回false
	 */
	public static boolean isEmpty(String s){
		return s==null || "".equals(s) || "null".equals(s) || "undefind".equals(s) || "undefined".equals(s);
	}

	/**
	 * 字符串转换为字符串数组
	 * @param str 字符串
	 * @param splitRegex 分隔符
	 * @return
	 */
	public static String[] str2StrArray(String str,String splitRegex){
		if(isEmpty(str)){
			return null;
		}
		return str.split(splitRegex);
	}

	/**
	 * 用默认的分隔符(,)将字符串转换为字符串数组
	 * @param str	字符串
	 * @return
	 */
	public static String[] str2StrArray(String str){
		return str2StrArray(str,",\\s*");
	}



	/**
	 * 按照yyyy-MM-dd HH:mm:ss的格式，日期转字符串
	 * @param date
	 * @return yyyy-MM-dd HH:mm:ss
	 */
	public static String date2Str(Date date){
		return date2Str(date,"yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 按照yyyy-MM-dd HH:mm:ss的格式，字符串转日期
	 * @param date
	 * @return
	 */
	public static Date str2Date(String date){
		if(yftools.notEmpty(date)){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				return sdf.parse(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return new Date();
		}else{
			return null;
		}
	}

	/**
	 * 按照参数format的格式，日期转字符串
	 * @param date
	 * @param format
	 * @return
	 */
	public static String date2Str(Date date,String format){
		if(date!=null){
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			return sdf.format(date);
		}else{
			return "";
		}
	}

	/**
	 * 把时间根据时、分、秒转换为时间段
	 * @param StrDate
	 */
	public static String getTimes(String StrDate){
		String resultTimes = "";

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date now;

		try {
			now = new Date();
			Date date=df.parse(StrDate);
			long times = now.getTime()-date.getTime();
			long day  =  times/(24*60*60*1000);
			long hour = (times/(60*60*1000)-day*24);
			long min  = ((times/(60*1000))-day*24*60-hour*60);
			long sec  = (times/1000-day*24*60*60-hour*60*60-min*60);

			StringBuffer sb = new StringBuffer();
			//sb.append("发表于：");
			if(hour>0 ){
				sb.append(hour+"小时前");
			} else if(min>0){
				sb.append(min+"分钟前");
			} else{
				sb.append(sec+"秒前");
			}

			resultTimes = sb.toString();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return resultTimes;
	}


	public static int compare_date(String DATE1, String DATE2) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date dt1 = df.parse(DATE1);
			Date dt2 = df.parse(DATE2);
			if (dt1.getTime() > dt2.getTime()) {
				System.out.println("dt1比dt2晚");
				return 1;
			} else if (dt1.getTime() < dt2.getTime()) {
				System.out.println("dt1=2017,d2=2018");
				return -1;
			} else {
				return 0;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return 0;
	}


	public static int daysBetweenHours(String startTime, String endTime) {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH");
		Calendar cal = Calendar.getInstance();
		long time1 = 0;
		long time2 = 0;

		try{
			cal.setTime(sdf.parse(startTime));
			time1 = cal.getTimeInMillis();
			cal.setTime(sdf.parse(endTime));
			time2 = cal.getTimeInMillis();
		}catch(Exception e){
			e.printStackTrace();
		}
		long between_days=(time2-time1)/(1000*3600);

		return Integer.parseInt(String.valueOf(between_days));
	}

	public static int daysBetweenMinute(String startTime, String endTime) {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		long time1 = 0;
		long time2 = 0;

		try{
			cal.setTime(sdf.parse(startTime));
			time1 = cal.getTimeInMillis();
			cal.setTime(sdf.parse(endTime));
			time2 = cal.getTimeInMillis();
		}catch(Exception e){
			e.printStackTrace();
		}
		long between_days=(time2-time1)/(1000*60);
		System.out.println("between_days="+between_days);
		return Integer.parseInt(String.valueOf(between_days))+1;
	}

	public static String daysBetweenhhmmss(String startTime, String endTime) throws Exception {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date parse = sdf.parse(startTime);
		Date date = sdf.parse(endTime);
		long between = date.getTime() - parse.getTime();
//		long day = between / (24 * 60 * 60 * 1000);
//		long hour = (between / (60 * 60 * 1000) - day * 24);
		long hour = (between / (60 * 60 * 1000));
//		long min = ((between / (60 * 1000)) - day * 24 * 60 - hour * 60);
		long min = ((between / (60 * 1000)) - hour * 60);
//		long s = (between / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
		long s = (between / 1000 - hour * 60 * 60 - min * 60);
//		System.out.println(day + "天" + hour + "小时" + min + "分" + s + "秒");

		return hour + ":" + min + ":" + s;

	}

	public static String daysBetweenss(String startTime, String endTime) throws Exception {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date parse = sdf.parse(startTime);
		Date date = sdf.parse(endTime);
		long between = date.getTime() - parse.getTime();
//		long day = between / (24 * 60 * 60 * 1000);
//		long hour = (between / (60 * 60 * 1000) - day * 24);
//		long hour = (between / (60 * 60 * 1000));
//		long min = ((between / (60 * 1000)) - day * 24 * 60 - hour * 60);
//		long min = ((between / (60 * 1000)) - hour * 60);
//		long s = (between / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
//		long s = (between / 1000 - hour * 60 * 60 - min * 60);
//		System.out.println(day + "天" + hour + "小时" + min + "分" + s + "秒");

		return between+"";

	}

	public static void main(String[] args) throws Exception {
		String aa="a,b,c,d";
		String[] a= aa.split(",");
		ArrayUtil.isNotEmpty(a);
		System.out.println(getPrizeMoney(5.00)+"");
//		System.out.println(daysBetweenMinute("2018-12-26 19:00:57","2018-12-26 19:00:57"));
//		System.out.println(UUID.randomUUID().toString().hashCode());
//		System.out.println(DateUtil.getAfterMinuteTime("2018-12-26 19:00:58",15));
//		System.out.println(compare_date("2018-12-26 19:00:55","2018-12-26 19:00:58"));
//		System.out.println(daysBetweenhhmmss("2018-12-27 19:00:57","2018-12-27 19:00:58"));

//		for(int i=0;i<10000;i++){
////			Random random = new Random();
////			int ii = random.nextInt(9999);
////			System.out.println((double)ii/100);
//			System.out.println(getPrizeMoney(5.00)+"");
//
//		}

//		System.out.println(getPrizeMoney(5.00)+"");

//		double getFirstCutMaxMoney=80;
//		double getFirstCutMinMoney=60;
//		double getEndPrice=10;
//		double getPrice=80;
//
//
//		Random random = new Random();
//		double max=getFirstCutMaxMoney-getFirstCutMinMoney;
//		double cutPrice = (double)random.nextInt((int)(max*100))/100+getFirstCutMinMoney;
//		System.out.println(cutPrice);
//		int i=0;
//		while (true){
//			if (i<100){
//				if (BigDecimalUtil.sub(getPrice,cutPrice)<=getEndPrice){
//					cutPrice = (double)random.nextInt((int)(max*100))/100+getFirstCutMinMoney;
//				}else{
//					break;
//				}
//			}else{
//				cutPrice=0;
//				break;
//			}
//			i++;
//			System.out.println(cutPrice);
//		}

	}

	public static double getPrizeMoney(double maxMoney){
		Random random = new Random();
		double money = (double)random.nextInt((int)(maxMoney*100))/100;
		return money;
	}

	public static int daysBetweenSecond(String startTime, String endTime) {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		long time1 = 0;
		long time2 = 0;

		try{
			cal.setTime(sdf.parse(startTime));
			time1 = cal.getTimeInMillis();
			cal.setTime(sdf.parse(endTime));
			time2 = cal.getTimeInMillis();
		}catch(Exception e){
			e.printStackTrace();
		}
		long between_days=(time2-time1)/(1000);

		return Integer.parseInt(String.valueOf(between_days));
	}



	public static String DateAdd(int days) {
		Calendar calendar2 = Calendar.getInstance();
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
		calendar2.add(Calendar.DATE, days);
		String three_days_after = sdf2.format(calendar2.getTime());
		return three_days_after;
	}


//	public static String getExtensionName(String filename) {
//		if ((filename != null) && (filename.length() > 0)) {
//			int dot = filename.lastIndexOf('.');
//			if ((dot >-1) && (dot < (filename.length() - 1))) {
//				return "."+filename.substring(dot + 1);
//			}
//		}
//		return "."+filename;
//	}

}
