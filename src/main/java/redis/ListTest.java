package redis;

import java.util.List;

import org.junit.Test;

import redis.clients.jedis.Jedis;

/**
 * 链表：插入、删除快，时间复杂度为O(1)；索引定位慢O(n)
 * 链表为双端可以进行左右端的操作l/r
 * 可以作为队列(一边进另一边出)、栈(同一边进、出数据)
 * push 添加数据
 * pop 弹出数据,，当数据全部弹出，该数据结构自动被删除
 * @author wsz
 * @date 2018年9月11日
 */
public class ListTest {
	
	private Jedis jedis = new Jedis("127.0.0.1", 6379);
	
	private final String LIST_KEY = "list_key"; 
	
	@Test
	public void add() {
		// 右端push数据：4 3 2 1 0
//		Long rpush = jedis.rpush(LIST_KEY, "4", "3","2", "1");
//		System.out.println(rpush);
//		jedis.rpush(LIST_KEY, "0");
		// 左端push数据: 1 2 3 4 4 3 2 1 0
//		Long lpush = jedis.lpush(LIST_KEY, "4", "3", "2", "1");
//		System.out.println(lpush);
	}
	
	@Test
	public void get() {// 1 2 3 4 4 3 2 1 0
		// 根据下标位置获取，遍历链表-查询效率低O(n);下标N可以为负数,index=-1标识倒数第一个元素,index=-2标识倒数第二个元素
		// 2
		String getByIndex = jedis.lindex(LIST_KEY, 1);
		System.out.println(getByIndex);
		// 0
		System.out.println(jedis.lindex(LIST_KEY, -1));
		// 1
		System.out.println(jedis.lindex(LIST_KEY, -2));
		
		// 获取所有元素O(n)
		List<String> all = jedis.lrange(LIST_KEY, 0, -1);
		System.out.println(all.toString());
		// 获取链表的长度
		System.out.println("len:"+jedis.llen(LIST_KEY));
		// 获取下标范围[m,n]内的数据
		// 2 3 4
		List<String> subList = jedis.lrange(LIST_KEY, 1, 3);
		System.out.println(subList.toString());
		// 2 3 4 4 3 2
		System.out.println(jedis.lrange(LIST_KEY, 1, -3).toString());
		
		// 左端弹出一个数据
//		String lpop = jedis.lpop(LIST_KEY);
		//1
//		System.out.println("lpop:"+lpop);
//		
		// 右端弹出一个数据
//		String rpop = jedis.rpop(LIST_KEY);
		// 0
//		System.out.println("rpop:"+rpop);
		
		// 保留区间[m,n]内的数据，其余删除；返回
		String ltrim = jedis.ltrim(LIST_KEY, 1, 4);
		System.out.println(ltrim);
	}
	
	/**
	 * 模拟队列，一端进一端出
	 * @throws InterruptedException 
	 */
	@Test
	public void queueTest() throws InterruptedException {
//		Long list = jedis.lpush(LIST_KEY, "a", "b", "c", "d", "e", "f");
//		System.out.println(list);
		int count = 0;
		int size = 100;
		while(count <= size) {
			jedis.lpush(LIST_KEY, String.valueOf(count));
			count++;
		}
		System.out.println(jedis.lrange(LIST_KEY, 0, -1));
		while(count>0) {
			Thread.sleep(200);
			String rpop = jedis.rpop(LIST_KEY);
			System.out.println(rpop);
			count--;
		}
	}
	
	/**
	 * 模拟栈,同一端进、出
	 * @throws InterruptedException 
	 */
	@Test
	public void stackTest() throws InterruptedException {
		int count = 0;
		int size = 100;
		while(count <= size) {
			jedis.lpush(LIST_KEY, String.valueOf(count));
			count++;
		}
		while( count>0 ) {
			Thread.sleep(200);
			String lpop = jedis.lpop(LIST_KEY);
			System.out.println(lpop);
			count--;
		}
	}
	
}
