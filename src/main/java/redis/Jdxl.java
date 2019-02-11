package redis;

import java.io.IOException;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

/**
 * 简单限流
 * @author wsz
 * @date 2018年11月19日
 */
public class Jdxl {

	Jedis jedis = new Jedis("127.0.0.1", 6379);
	
	String userid = "user";
	String action = "add";
	String key = userid + action;
	//60秒内操作不能超过10次
	int period = 60;
	int maxCount = 10;
	
	public boolean isActionAllowed() {
		long time = System.currentTimeMillis();
		Pipeline pipe = jedis.pipelined();
		pipe.multi();
		pipe.zadd(key, time, ""+time);
		//删除60s之前的数据
		pipe.zremrangeByScore(key, 0, time-period*1000);
		//获取查询范围内的操作次数
		Response<Long> count = pipe.zcard(key);
		pipe.exec();
		try {
			pipe.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return count.get() <= maxCount;
	}
	
	public static void main(String[] args) {
		Jdxl xl = new Jdxl();
		for(int i =0; i<11; i++) {
			try {
				Thread.sleep(500);
				System.out.println(xl.isActionAllowed());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
