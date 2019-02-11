package redis;

import java.util.List;

import org.junit.Test;

import redis.clients.jedis.GeoCoordinate;
import redis.clients.jedis.GeoRadiusResponse;
import redis.clients.jedis.GeoUnit;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.geo.GeoRadiusParam;

/**
 * 地理位置
 * @author wsz
 * @date 2018年11月19日
 */
public class GeoHash {

	Jedis jedis = new Jedis("127.0.0.1", 6379);
	
	String key = "company";
	/**
	 * 新增坐标点元素
	 */
//	@Test
	public void geoAdd() {
		jedis.geoadd(key, 116.48105, 39.996794, "juejin");
		jedis.geoadd(key, 116.514203, 39.905409, "ireader");
		jedis.geoadd(key, 116.489033, 40.007669, "meituan");
		
		jedis.geoadd(key, 116.589033, 40.107669, "a");
		jedis.geoadd(key, 116.689033, 40.207669, "b");
		jedis.geoadd(key, 116.789033, 40.307669, "c");
		jedis.geoadd(key, 116.689033, 40.257669, "d");
	}
	
	/**
	 * geo的存储结构为zset，使用zrem进行删除
	 */
//	@Test
	public void geoDelete() {
		Long zrem = jedis.zrem(key, "meituan");
		System.out.println(zrem);
	}
	
	/**
	 * 获取两个元素之间的距离
	 */
//	@Test
	public void distance() {
		Double geodist = jedis.geodist(key, "juejin", "ireader", GeoUnit.KM);
		Double geodist2 = jedis.geodist(key, "juejin", "meituan", GeoUnit.KM);
		Double geodist3 = jedis.geodist(key, "meituan", "meituan", GeoUnit.M);
		System.out.println(geodist+" "+geodist2+" "+geodist3);
	}
	
	/**
	 * 获取元素的经纬度坐标：坐标数据将出现误差
	 */
//	@Test
	public void geoPos() {
		List<GeoCoordinate> geopos = jedis.geopos(key, "juejin", "ireader", "meituan");
		System.out.println(geopos);
		
		//geohash.org/${hash}进行搜索查看
		List<String> geohash = jedis.geohash(key, "juejin");
		System.out.println(geohash);
	}
	/**
	 * 查询某一元素附近的其他元素
	 */
//	@Test
	public void rangeDistance() {
		//参数对象:asc/desc/count/withDist获取距离/
		GeoRadiusParam params = GeoRadiusParam.geoRadiusParam();
		params.sortDescending();
//		params.count(2);
		params.withDist();
		
		List<GeoRadiusResponse> georadiusByMember = jedis.georadiusByMember(key, "ireader", 200, GeoUnit.KM, params);
		for (GeoRadiusResponse res : georadiusByMember) {
			System.out.println(res.getMemberByString()+" "+res.getDistance());
		}
	}
	
	/**
	 * 根据某一个坐标点查询附近的元素信息
	 */
	@Test
	public void rangePoint() {
		GeoRadiusParam params = GeoRadiusParam.geoRadiusParam();
		params.sortDescending();
		params.withDist();
		params.count(2);
		
		List<GeoRadiusResponse> georadius = jedis.georadius(key, 116.48105, 39.996794, 50, GeoUnit.KM, params);
		for (GeoRadiusResponse res : georadius) {
			System.out.println(res.getMemberByString()+" "+res.getDistance());
		}
	}
}
