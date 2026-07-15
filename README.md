
# sa-prepop-api-tests

This is the API acceptance test repository for Individual Benefits/Employments/Income/Tax/National Insurance.

This tests maximum and minimum data HAPPY_PATHs from the stubs and an error case if appropriate

## How to run a normal suite in local:

```shell
sm2 --start API_SA_PREPOP_ALL
```

```shell
./run_tests_local.sh
```

## How to run zap tests

```shell
sm2 --start API_SA_PREPOP_ALL
```

The `run_zap_tests_local.sh` file now uses [dast-config-manager](https://github.com/hmrc/dast-config-manager) to run ZAP tests locally using the DAST Docker image.

## Licence

This code is open source software licensed under the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0.html).