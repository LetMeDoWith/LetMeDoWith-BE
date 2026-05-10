# Guide: Batch Layer

## DDD in Batch
The same domain rules apply. Business logic belongs in domain entities and `@DomainService` classes — not in Tasklets or Processors.
Tasklets and Steps are orchestration, not business logic containers.

## Tasklet vs Chunk
- Current codebase: Tasklet-based only.
- Chunk-based is also a valid option when processing large datasets record-by-record.
- The choice between Tasklet and Chunk is a design decision — read `.agents/skills/api-development-plan/SKILL.md` before deciding.

## Job Configuration Pattern

```java
@Configuration
@RequiredArgsConstructor
public class XyzJobConfig {
    private static final String JOB_NAME = "xyzJob";
    private static final String STEP_NAME = "xyzStep";

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    @Bean
    public Job xyzJob(Step xyzStep) {
        return new JobBuilder(JOB_NAME, jobRepository)
            .incrementer(new RunIdIncrementer())
            .start(xyzStep)
            .build();
    }

    @Bean
    @JobScope
    public Step xyzStep(XyzTasklet tasklet) {
        return new StepBuilder(STEP_NAME, jobRepository)
            .tasklet(tasklet, platformTransactionManager)
            .build();
    }
}
```

## Scheduler Pattern

```java
@Component
@RequiredArgsConstructor
@Slf4j
public class XyzJobScheduler {
    private final JobLauncher jobLauncher;

    @Qualifier("xyzJob")
    private final Job xyzJob;

    @Scheduled(cron = "...")
    public void runXyzJob() {
        LocalDateTime executionDateTime = SystemTimeUtil.now();

        // Optional guard clause — skip if condition not met
        if (!shouldRun(executionDateTime)) {
            log.info("Skip xyzJob. executionDateTime={}", executionDateTime);
            return;
        }

        JobParameters jobParameters = new JobParametersBuilder()
            .addLong("run.id", System.currentTimeMillis())
            .addLocalDateTime("executionDateTime", executionDateTime)
            .toJobParameters();

        try {
            JobExecution jobExecution = jobLauncher.run(xyzJob, jobParameters);
            if (jobExecution.getStatus() != BatchStatus.COMPLETED) {
                throw new IllegalStateException("xyzJob failed. status=" + jobExecution.getStatus());
            }
        } catch (Exception e) {
            log.error("Failed to run xyzJob", e);
            throw new IllegalStateException("Failed to run xyzJob", e);
        }
    }
}
```

## Rules
- `spring.batch.job.enabled=false` in all profiles — jobs run only via `@Scheduled` schedulers, never on startup
- Always use `SystemTimeUtil.now()` instead of `LocalDateTime.now()` (clock is mockable in tests)
- Always include `run.id` (currentTimeMillis) in `JobParameters` to allow re-runs
- Always check `jobExecution.getStatus() != BatchStatus.COMPLETED` and throw if not completed
- Use `@Qualifier` when injecting a specific `Job` bean into the scheduler
- Job and Step names must be unique across the application (constants preferred)
