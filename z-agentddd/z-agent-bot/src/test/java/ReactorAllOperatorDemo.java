import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.Duration;
import java.util.function.Consumer;

public class ReactorAllOperatorDemo {

    public static void main(String[] args) {
        System.out.println("===== 1. doOnNext + then 演示 =====");
        demo1_doOnNext_then();

        System.out.println("\n===== 2. Mono.just() vs Mono.defer() 演示 =====");
        demo2_just_vs_defer();

        System.out.println("\n===== 3. flatMap 演示 =====");
        demo3_flatMap();

        System.out.println("\n===== 4. flatMapMany 演示 =====");
        demo4_flatMapMany();

        System.out.println("\n===== 5. onErrorResume 演示 =====");
        demo5_onErrorResume();

        System.out.println("\n===== 6. block() 同步阻塞演示 =====");
        demo6_block();
    }

    // 1. doOnNext（每个数据都走） + then（流结束才走）
    private static void demo1_doOnNext_then() {
        Flux.just("我", "是", "流", "片", "段")
                .doOnNext(item -> System.out.println("处理片段：" + item))
                .then(Mono.just("【全部处理完成，返回最终结果】"))
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) {
                        System.out.println(s);
                    }
                });
    }

    // 2. just（立刻执行） vs defer（订阅时才执行）
    private static void demo2_just_vs_defer() {
        Mono<Long> justMono = Mono.just(System.currentTimeMillis());
        Mono<Long> deferMono = Mono.defer(() -> Mono.just(System.currentTimeMillis()));

        // 等待1秒
        try { Thread.sleep(1000); } catch (Exception ignored) {}

        justMono.subscribe(val -> System.out.println("just: " + val + "（值被提前缓存）"));
        deferMono.subscribe(val -> System.out.println("defer: " + val + "（每次都重新计算）"));
    }

    // 3. flatMap：用前一个结果做下一件事
    private static void demo3_flatMap() {
        Mono.just("Hello")
                .flatMap(str -> Mono.just(str + " + Reactor"))
                .subscribe(System.out::println);
    }

    // 4. flatMapMany：1个值 → 展开成多个值（Flux）
    private static void demo4_flatMapMany() {
        Mono.just("开始")
                .flatMapMany(ignore -> Flux.just(10, 20, 30, 40))
                .subscribe(val -> System.out.println("输出：" + val));
    }

    // 5. onErrorResume：出错了不崩，给默认值
    private static void demo5_onErrorResume() {
        Mono.error(new RuntimeException("模拟报错"))
                .onErrorResume(error -> Mono.just("出错了，我是默认兜底内容"))
                .subscribe(System.out::println);
    }

    // 6. block：异步变同步，卡住等结果
    private static void demo6_block() {
        System.out.println("block 开始等待...");
        String result = Mono.just("我是block结果")
                .delayElement(Duration.ofSeconds(1)) // 模拟延迟1秒
                .block(); // 阻塞等待
        System.out.println(result);
    }
}