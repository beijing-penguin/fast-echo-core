package com.dc.im.core;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dc.echo.config.LoggerName;
/**
 * mqbroker.cmd -n 127.0.0.1:9876 autoCreateTopicEnable=true
 * mqnamesrv.cmd
 * @author Administrator
 *
 */
public class Trade {
	private static Logger LOG = LoggerFactory.getLogger(LoggerName.CONSOLE);
	private static DefaultMQProducer producer;
	// 先设定写入10000行数据
	private static final int COUNT = 100000;
	static {
		producer = new DefaultMQProducer("please_rename_unique_group_name");
		producer.setNamesrvAddr("localhost:9876");
		try {
			producer.start();
		} catch (MQClientException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 比较时间
	 * @throws Exception 
	 */
	public void compareTime() throws Exception {
		System.out.println("====start======");
		useFileOStream();
		useBufferOStream();
		useBufferedWriter();
		useRandomAccessFile();
		useLog();
		//useRocketMq();
		System.out.println("=====end========");
	}
	public void useRocketMq() throws Exception {

		long begin = System.currentTimeMillis();
		for (int i = 0; i < COUNT; i++) {
			Message msg = new Message("TopicTest",
					"TagA",
					("java--测试写文件 fos").getBytes(RemotingHelper.DEFAULT_CHARSET)
					);
			SendResult sendResult = producer.send(msg);
			//System.out.printf("%s%n", sendResult);
		}
		System.out.println("useRocketMq执行耗时: " + (System.currentTimeMillis() - begin));
	}

	public void useLog() throws Exception {
		long begin = System.currentTimeMillis();
		for (int i = 0; i < COUNT; i++) {
			LOG.info("=java--测试写文件 fos");
		}
		System.out.println("useLog执行耗时: " + (System.currentTimeMillis() - begin));
	}
	public void useRandomAccessFile() {
		ExecutorService service = new ThreadPoolExecutor(2000, 5000, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

		File file = new File("test04.txt");
		try {
			final RandomAccessFile fout = new RandomAccessFile(file, "rw");
			long filelength = fout.length();//获取文件的长度
			fout.seek(filelength);//将文件的读写指针定位到文件的末尾
			final FileChannel fcout = fout.getChannel();//打开文件通道
			long begin = System.currentTimeMillis();
			for (int i = 0; i < COUNT; i++) {
				FileLock flout = null;
				while (flout==null) {
					try {
						flout = fcout.tryLock();//不断的请求锁，如果请求不到，等一秒再请求
					} catch (Exception e) {
						//System.out.print("lock is exist ......");
					}
				}
				String str = "java--测试写文件 fos\\r\\n";//需要写入的内容

				try {
					fout.write(str.getBytes());
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					flout.release();
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
			fcout.close();
			fout.close();
			System.out.println("RandomAccessFile执行耗时: " + (System.currentTimeMillis() - begin));
		} catch (IOException e1) {
			e1.printStackTrace();
			System.out.print("file no find ...");
		} catch (Exception e) {
			e.printStackTrace();
		} 

	}
	/**
	 * 使用FileOutputStream
	 */
	public void useFileOStream() {
		try {
			File file = new File("text01.txt");
			if (!file.exists()) {
				file.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(file,true);

			long begin = System.currentTimeMillis();
			for (int i = 0; i < COUNT; i++) {
				fos.write("java--测试写文件 fos\r\n".getBytes());
				fos.flush();
			}
			fos.close();
			System.out.println("FileOutputStream执行耗时: " + (System.currentTimeMillis() - begin));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 使用BufferedOutputStream
	 */
	public void useBufferOStream() {
		try {
			File file = new File("text02.txt");
			if (!file.exists()) {
				file.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(file,true);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			long begin = System.currentTimeMillis();
			for (int i = 0; i < COUNT; i++) {
				bos.write("java--测试写文件 fos\r\n".getBytes());
				bos.flush();
			}
			bos.close();
			System.out.println("BufferedOutputStream执行耗时: " + (System.currentTimeMillis() - begin));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 使用Filewriter
	 */
	public void useBufferedWriter() {
		try {
			File file = new File("text03.txt");
			if (!file.exists()) {
				file.createNewFile();
			}
			// 一次写入的文件大小小于10M时， bufferedWriter并不能显著降低时间,而且此时BufferedOutputStream仍是占优的
			FileWriter fWriter = new FileWriter(file,true);
			BufferedWriter bufferedWriter = new BufferedWriter(fWriter);
			long begin = System.currentTimeMillis();
			for (int i = 0; i < COUNT; i++) {
				bufferedWriter.write("java--测试写文件 fos\r\n");
				bufferedWriter.flush();
			}
			bufferedWriter.close();
			System.out.println("BufferedWriter 执行耗时: " + (System.currentTimeMillis() - begin));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) throws Exception {
		Trade trade = new Trade();
		// 测试写入操作的速度
		for (int i = 0; i < 100; i++) {
			trade.compareTime();
		}
		// 测试多线程并发加锁后的写入操作的速度
		//        for (int i = 0; i < 10; i++) {
		//            trade.lockCompareTime();
		//        }
	}
	private void lockCompareTime() throws Exception {
		//useRandomAccessFile();
		useLog();
	}
}
