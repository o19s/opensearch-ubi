To create the CloudFormation stack, create a file `stack.properties` in this directory with the following contents:

```
REGION="us-east-1"
PROFILE=""
KEYNAME=""
```

Set an appropriate value for each property:

* `REGION` - The AWS region to deploy in.
* `PROFILE` - The AWS CLI profile name.
* `KEYNAME` - The name of the SSH key to use for the EC2 instances.

Now you can create the stack with:

```
./create-stack
```
