#include <linux/module.h>
#include <linux/vmalloc.h>
#include <linux/fs.h>
#include <linux/platform_device.h>
#include <linux/miscdevice.h>
#include <linux/gpio.h>
#include <linux/io.h>
#include <linux/sched.h>   //wake_up_process()    
#include <linux/kthread.h> //kthread_create()¡¢kthread_run()  
#include <linux/err.h> //IS_ERR()¡¢PTR_ERR()  
#include <linux/delay.h>
#include <linux/poll.h>
#include <linux/slab.h>
#include <mach/platform.h>
#include <mach/gpio.h>
#include <asm/uaccess.h>

#define TRUE	1
#define FALSE	0

MODULE_LICENSE("GPL");

#define ADDR_GPIOG_DATA		(SW_VA_PORTC_IO_BASE + 0XE8)

#define DEBUG
#ifdef DEBUG
#define	PRINT(args...)	printk(KERN_ALERT args)
#else
#define	PRINT(args...)	do{}while(0)

#endif


//ioctl cmd
enum{
	GPIO_PLAY = 0x01,
	GPIO_PLAY_ERR = 0x02,
	GPIO_RFID_CARD1 = 0x03,
	GPIO_RFID_CARD2 = 0x04,
	GPIO_RFID_CARD3 = 0x05,
	GPIO_RFID_CARD4 = 0x06,
	GPIO_RFID_CARD5 = 0x07,
	GPIO_RFID_CARD6 = 0x08,
	GPIO_RFID_CARD7 = 0x09,
	GPIO_RFID_CARD8 = 0x0A,
	GPIO_IDLE = 0x0B,
};

// 3-8译码器真值表
enum{
	STATE_RFID_IDLE = 0x00,
	STATE_RFID_CARD1 = 0x00, // 切换为RFID1
	STATE_RFID_CARD2 = 0x01, // 切换为RFID2
	STATE_RFID_CARD3 = 0x02, // 切换为RFID3
	STATE_RFID_CARD4 = 0x03, // 切换为RFID4
	STATE_RFID_CARD5 = 0x04, // 切换为RFID4
	STATE_RFID_CARD6 = 0x05, // 切换为RFID4
	STATE_RFID_CARD7 = 0x06, // 切换为RFID4
	STATE_RFID_CARD8 = 0x07, // 切换为RFID4
	
};

static struct gpio gpio_array[]={
		//data bus 16bits
		{GPIOG(4), 	GPIOF_DIR_OUT, NULL},
		{GPIOG(5), 	GPIOF_DIR_OUT, NULL},
		{GPIOG(8), 	GPIOF_DIR_OUT, NULL},
		{GPIOG(9), 	GPIOF_DIR_OUT, NULL},
};

static void play() {
	
	__gpio_set_value(GPIOG(4),1);
	msleep(100);
	__gpio_set_value(GPIOG(4),0);
}

static void playErr() {
	printk(KERN_ALERT "--->playErr\n");
	int i=0;
	for(i = 0; i< 3; i++) {
		__gpio_set_value(GPIOG(4),1);
		msleep(100);
		__gpio_set_value(GPIOG(4),0);
		msleep(200);
	}
}

static void setState(int state) {
	unsigned long regdata = 0;
	if(state < 0 || state >= 8) {
		return;
	}
	
	
	regdata = readl(ADDR_GPIOG_DATA);
	PRINT(KERN_ALERT "setState regdata=0x%x  %d\n", regdata, state);
	
	if ((state & 0x04) == 0x04)
	{
		regdata = regdata | (0x0200);

	} else {
		//__gpio_set_value(GPIOG(9), 0);
		regdata = regdata & ~(0x0200);
	}
	if ((state & 0x02) == 0x02)
	{
		//__gpio_set_value(GPIOG(8), 1);
		regdata = regdata | (0x0100);
	} else {
		// __gpio_set_value(GPIOG(8), 0);
		regdata = regdata & ~(0x0100);
	}
	if ((state & 0x01) == 0x01)
	{
		// __gpio_set_value(GPIOG(5), 1);
		regdata = regdata | (0x020);
	} else {
		// __gpio_set_value(GPIOG(5), 0);
		regdata = regdata & ~(0x020);
	}
	writel(regdata,ADDR_GPIOG_DATA);
}

/*****************
* test file node
******************/

static void playClick(struct device *dev,  
          struct device_attribute *attr,  
          __user const char *buf, size_t count){
	play();
}

static void setStat(struct device *dev,  
          struct device_attribute *attr,  
          __user const char *buf, size_t count){
	setState(3);
}

static void test(struct device *dev,  
          struct device_attribute *attr,  
          __user const char *buf, size_t count) {
    unsigned long regdata = 0;
	regdata = readl(ADDR_GPIOG_DATA);
	regdata |= 0x03f0;
	writel(regdata,ADDR_GPIOG_DATA);
}

static DEVICE_ATTR(playClick, S_IRUGO | S_IWUSR, NULL, playClick);
static DEVICE_ATTR(setState, S_IRUGO | S_IWUSR, NULL, setStat);
static DEVICE_ATTR(test, S_IRUGO | S_IWUSR, NULL, test);


static struct attribute *sysfs_entries[] = {
	&dev_attr_playClick.attr,
	&dev_attr_setState.attr,
	&dev_attr_test.attr,
	NULL
};


static struct attribute_group attribute_group = {
	.name = NULL,		/* put in device directory */
	.attrs = sysfs_entries,
};

int ext_gpio_open(struct inode *inode, struct file *filep)
{
	PRINT(KERN_ALERT "===>ext_gpio open\n");
	return 0;
}

ssize_t ext_gpio_read(struct file *filep, char __user *buff, size_t count, loff_t *offset)
{
	PRINT(KERN_ALERT "===>gpio read\n");
	return 0;
}

ssize_t ext_gpio_write(struct file *filep, const char __user *buff, size_t count, loff_t *offset)
{
	return 0;
}

/*
* Steps:
* 1. invoke write method writing data to driver buffer, data is ready
* 2. invoke ioctl method for starting print
* 3. Triger the interrupt_handler after starting 
*/	
void ext_gpio_ioctl(struct file *filep, unsigned int cmd, unsigned long arg)
{
	switch(cmd) {
		case GPIO_PLAY:
			play();
			break;
		case GPIO_PLAY_ERR:
			playErr();
			break;
		case GPIO_RFID_CARD1:
			setState(STATE_RFID_CARD1);
			break;
		case GPIO_RFID_CARD2:
			setState(STATE_RFID_CARD2);
			break;
		case GPIO_RFID_CARD3:
			setState(STATE_RFID_CARD3);
			break;
		case GPIO_RFID_CARD4:
			setState(STATE_RFID_CARD4);
			break;
			
		case GPIO_RFID_CARD5:
			setState(STATE_RFID_CARD5);
			break;
		case GPIO_RFID_CARD6:
			setState(STATE_RFID_CARD6);
			break;
		case GPIO_RFID_CARD7:
			setState(STATE_RFID_CARD7);
			break;
		case GPIO_RFID_CARD8:
			setState(STATE_RFID_CARD8);
			break;
		case GPIO_IDLE:
			setState(STATE_RFID_IDLE);
			break;
		default:
			break;
	}
	// PRINT(KERN_ALERT "--->ioctl ok\n");
}

struct file_operations ext_gpio_fops={
	.owner = THIS_MODULE,
	.open = ext_gpio_open,
	.read = ext_gpio_read,
	.write = ext_gpio_write,
	.unlocked_ioctl = ext_gpio_ioctl,
};

struct miscdevice ext_gpio_dev={
	.minor = MISC_DYNAMIC_MINOR,
	.name = "ext-gpio",
	.fops = &ext_gpio_fops,
	.mode = 0x766,
};


static int ext_gpio_probe(struct platform_device* dev)
{
	int result;
	PRINT(KERN_ALERT "===> ext_gpio probe, %d\n",sizeof(char));
	misc_register(&ext_gpio_dev);

	/*init wait queue*/
	int error = gpio_request_array(&gpio_array,ARRAY_SIZE(gpio_array));
	if(error != 0)
	{
		PRINT(KERN_ALERT "===> ext_gpio probe request=%d\n",error);
		result = -ENODEV;
		goto fail_probe;
	}

	return 0;
fail_probe:
	misc_deregister(&ext_gpio_dev);
	return result;
}


static int ext_gpio_remove(struct platform_device* dev)
{
	PRINT(KERN_ALERT "===>ext_gpio remove\n");
	gpio_free_array(&gpio_array, ARRAY_SIZE(gpio_array));
	return 0;
}

static int ext_gpio_suspend(struct platform_device *dev, pm_message_t state)
{
	PRINT(KERN_ALERT "===>ext_gpio suspend\n");
	return 0;
}

static int ext_gpio_resume(struct platform_device* dev)
{
	PRINT(KERN_ALERT "===>ext_gpio resume\n");
	return 0;
}

struct platform_device ext_gpio_device={
	.name = "ext-gpio",
	.id = -1,
};

struct platform_driver ext_gpio_driver={
	.probe = ext_gpio_probe,
	.remove = ext_gpio_remove,
	.suspend = ext_gpio_suspend,
	.resume = ext_gpio_resume,
	.driver={
		.name = "ext-gpio",
		.owner = THIS_MODULE,
	},
};

static int __init ext_gpio_init(void)
{
	int ret=0;
	if(platform_device_register(&ext_gpio_device))
	{
		PRINT(KERN_ALERT "register exit_gpio device failed\n");
		goto init_fail;
	}
	if(platform_driver_register(&ext_gpio_driver))
	{
		platform_device_unregister(&ext_gpio_device);
	}
	//kFpgaData.buffer = vmalloc(TOTAL_BUFFER_SIZE);
	ret = sysfs_create_group(&ext_gpio_device.dev.kobj,
			&attribute_group);

	return 0;
init_fail:
	return -1;
}

static int __exit ext_gpio_exit(void)
{
	platform_driver_unregister(&ext_gpio_driver);
	platform_device_unregister(&ext_gpio_device);
}	


module_init(ext_gpio_init);
module_exit(ext_gpio_exit);

MODULE_AUTHOR("Kevin Zhao");



