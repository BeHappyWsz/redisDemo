package redis;

import java.util.Set;

import org.junit.Test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

/**
 * 搜查特定的key
 * @author wsz
 * @date 2018年11月19日
 */
public class ScanTest {

	Jedis jedis = new Jedis("127.0.0.1", 6379);
	
	/**
	 * keys遍历算法O(n)效率低
	 */
//	@Test
	public void keysScan() {
		Set<String> keys = jedis.keys("c*");
		System.out.println(keys);
		Set<String> keys2 = jedis.keys("*c");
		System.out.println(keys2);
		Set<String> keys3 = jedis.keys("*o*");
		System.out.println(keys3);
	}
	
	/**
	 * scan复杂度O(n),游标分布进行不阻塞线程,limit参数控制结果集
	 * 结果可能出现重复需要去重
	 */
	@Test
	public void scanTest() {
		ScanParams params = new ScanParams();
		params.match("c*");
		params.count(10);
		
		ScanResult<String> scan = jedis.scan("0", params);
		System.out.println(scan.getResult());
		
		//获取key的类型
		String type = jedis.type("company");
		System.out.println(type);
	}
}
