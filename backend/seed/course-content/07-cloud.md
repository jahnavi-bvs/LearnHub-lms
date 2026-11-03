# course: Cloud Computing Fundamentals

## module: Introduction to Cloud Computing
video: M988_fsOSWo

### Introduction
Cloud computing delivers servers, storage and software over the internet, on demand. Instead of buying hardware, you rent what you need and pay for what you use.

### Key concepts
- **On-demand self-service:** create resources in minutes.
- **Scalability:** add or remove capacity as needed.
- **Pay-as-you-go:** an operating cost rather than a large upfront purchase.
- **Global reach:** run services close to users worldwide.

### Definitions
- **Cloud provider:** a company offering these services, such as AWS, Azure or Google Cloud.
- **Data centre:** a facility that houses servers.
- **Elasticity:** automatically scaling with demand.
- **On-premises:** hardware you own and run yourself.

### Example
An exam-results website gets huge traffic for one day a year. On the cloud, it can add servers for that day and then remove them, rather than buying hardware that sits idle all year.

### Key takeaways
- The cloud trades upfront hardware cost for ongoing usage cost.
- Elasticity suits workloads that change over time.
- Unused resources still cost money, so monitor your spending.

## module: Cloud Service Models – IaaS, PaaS and SaaS
video: 36zducUX16w

### Introduction
Cloud services differ in how much the provider manages for you. The three main models trade control against convenience.

### Key concepts
- **IaaS:** rent virtual machines, and manage the OS and everything above it.
- **PaaS:** deploy your code, and the platform runs it.
- **SaaS:** use finished software through a browser.
- **Shared responsibility:** the more the provider manages, the less you do.

### Definitions
- **IaaS (Infrastructure as a Service):** for example AWS EC2.
- **PaaS (Platform as a Service):** for example Heroku or AWS Elastic Beanstalk.
- **SaaS (Software as a Service):** for example Gmail or Microsoft 365.

### Example
Think of pizza. IaaS is buying ingredients and baking at home with a rented kitchen. PaaS is a takeaway pizza you just serve. SaaS is eating at a restaurant, with everything done for you.

### Key takeaways
- More control means more responsibility.
- Choose the highest-level model that meets your needs.
- You are always responsible for your data and user access.

## module: Cloud Deployment Models
video: XlNR7myQydI

### Introduction
Deployment models describe where the cloud runs and who can use it. The choice depends on cost, control and regulation.

### Key concepts
- **Public cloud:** shared infrastructure run by a provider.
- **Private cloud:** dedicated to one organization.
- **Hybrid cloud:** combines public and private.
- **Multi-cloud:** uses more than one provider.

### Definitions
- **Tenant:** a customer sharing cloud infrastructure.
- **Data residency:** the rules on where data may be stored.
- **Vendor lock-in:** difficulty moving away from a provider.

### Example
A bank may keep customer records in a private cloud to meet regulations, and run its public website in a public cloud so it can handle traffic spikes. This is a hybrid model.

### Key takeaways
- Public cloud is usually the cheapest and quickest to start with.
- Private cloud offers more control but costs more.
- Hybrid suits organizations with strict data rules.

## module: Virtualization and Containers
video: eyNBf1sqdBQ

### Introduction
Virtualization lets one physical server act as many. Containers go further, making applications lightweight and easy to move between environments.

### Key concepts
- **Virtual machines:** each one has its own full operating system.
- **Containers:** share the host's kernel and start in seconds.
- **Docker:** builds and runs containers.
- **Kubernetes:** runs and manages many containers.

### Definitions
- **Hypervisor:** software that runs virtual machines.
- **Image:** a template for creating a container.
- **Container:** a running, isolated application.
- **Orchestration:** automating deployment and scaling of containers.

### Example
```dockerfile
FROM eclipse-temurin:25
COPY target/app.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```
This image runs the same way on a laptop and in the cloud.

### Key takeaways
- VMs give stronger isolation; containers are lighter and faster.
- Containers solve "it works on my machine".
- Many cloud platforms run containers on top of VMs.

## module: Introduction to Amazon Web Services (AWS)
video: Nzv-tzU-UAw

### Introduction
AWS is one of the largest cloud platforms, with services for computing, storage, databases and much more. A few core services cover most beginner needs.

### Key concepts
- **EC2:** virtual servers.
- **S3:** object storage for files.
- **RDS:** managed relational databases.
- **IAM:** users and permissions.
- **Regions and Availability Zones:** geographic locations and isolated data centres within them.

### Definitions
- **Region:** a geographic area containing AWS data centres.
- **Availability Zone:** one or more separate data centres in a region.
- **Free tier:** limited free usage for new accounts.
- **Instance:** a single virtual server.

### Example
A simple web application might run on an EC2 instance, store images in S3, and keep its data in an RDS MySQL database, with IAM controlling who has access to each.

### Key takeaways
- Set a billing alert as soon as you create an account.
- Enable MFA and don't use the root account day to day.
- Stop or delete resources after practice to avoid charges.

## module: Cloud Storage and Database Services
video: 6E30Yr2UATw

### Introduction
Cloud platforms offer different storage for different kinds of data. Choosing correctly affects cost, speed and how the system grows.

### Key concepts
- **Object storage (S3):** files accessed by key; highly durable.
- **Block storage (EBS):** disks attached to virtual servers.
- **Relational databases (RDS):** tables with SQL.
- **NoSQL databases (DynamoDB):** flexible, fast access at scale.
- **Managed services:** the provider handles backups and patching.

### Definitions
- **Durability:** how unlikely data loss is.
- **Read replica:** a copy of a database that serves reads.
- **Bucket:** a container for objects in S3.

### Example
A video-learning site stores video files in object storage, course and user data in a relational database, and fast-changing session data in a NoSQL database or cache.

### Key takeaways
- Don't store large files inside a relational database.
- Use a relational database when you need transactions and joins.
- Keep storage buckets private unless they must be public.

## module: Cloud Security and Identity Management
video: Qt9lhzFhW_c

### Introduction
Cloud security works differently from a private data centre. The provider secures the infrastructure, and you must secure what you put on it.

### Key concepts
- **Shared responsibility model:** divides duties between provider and customer.
- **IAM:** users, roles and policies.
- **Encryption:** protect data at rest and in transit.
- **Network controls:** security groups and private networks.
- **Logging:** record who did what, for example with CloudTrail.

### Definitions
- **IAM role:** a set of permissions that can be assumed temporarily.
- **Policy:** a document defining what's allowed.
- **Least privilege:** only the permissions required.
- **Misconfiguration:** an incorrect setting that creates exposure.

### Example
A public storage bucket is a common real-world mistake that has exposed private files. Keeping public access blocked by default and granting access through specific roles prevents it.

### Key takeaways
- Misconfiguration causes many cloud breaches.
- Use roles rather than long-lived access keys.
- Never commit cloud credentials to source control.

## module: Cloud Deployment and Practical Projects
video: 7Gym2XVcA5A

### Introduction
Deploying an application is where the cloud concepts come together. A small real deployment teaches more than reading about services.

### Key concepts
- **Basic deployment:** launch a server, install the runtime, and run the app.
- **Static hosting:** serve a site from storage and a CDN.
- **CI/CD:** build, test and deploy automatically.
- **Infrastructure as Code:** define resources in files, such as Terraform.
- **Load balancing:** spread traffic across servers.

### Definitions
- **CI/CD:** Continuous Integration and Continuous Delivery.
- **CDN:** a network that serves content from locations close to users.
- **Load balancer:** distributes requests between servers.

### Example
Deploying a website on EC2: launch an instance, allow ports 22 and 80 in the security group, connect by SSH, install a web server, and upload your files. Then visit the server's public address.

### Key takeaways
- Start by hosting a simple static website.
- Automate deployments once they work by hand.
- Restrict SSH access and delete practice resources when you finish.
