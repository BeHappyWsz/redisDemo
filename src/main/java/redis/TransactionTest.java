package redis;

import java.util.List;

import org.junit.Test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

public class TransactionTest {

	Jedis jedis = new Jedis("127.0.0.1", 6379);
	String key = "tran";
	
	@Test
	public void tranTest() {
		jedis.setnx(key, "5");
		System.out.println(doubleAccount());
		jedis.close();
	}
	
	public int doubleAccount() {
		while(true) {
			jedis.watch(key);
			int value = Integer.parseInt(jedis.get(key));
			value *=2;
			//watch后key值被修改将导致事务失败
			jedis.incr(key);
			Transaction tx = jedis.multi();
			tx.set(key, String.valueOf(value));
			List<Object> exec = tx.exec();
			if(exec != null) {
				System.out.println(exec);
				//执行成功
				break;
			}
		}
		return Integer.parseInt(jedis.get(key));
	}
}
