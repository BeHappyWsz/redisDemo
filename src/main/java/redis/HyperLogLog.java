package redis;

import redis.clients.jedis.Jedis;

/**
 * 
 * @author wsz
 * @date 2018年11月19日
 */
public class HyperLogLog {

	public static void main(String[] args) {
		Jedis jedis = new Jedis("127.0.0.1",6379);
	    for (int i = 0; i < 10000; i++) {
	      jedis.pfadd("codehole", "user" + i);
	    }
	    long total = jedis.pfcount("codehole");
	    System.out.println("10000-"+total);
	    jedis.close();
	}
}
