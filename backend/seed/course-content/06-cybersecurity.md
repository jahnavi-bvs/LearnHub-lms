# course: Cybersecurity Fundamentals

## module: Introduction to Cybersecurity
video: inWWhr5tnEA

### Introduction
Cybersecurity protects systems, networks and data from attack and damage. As more of life moves online, it matters to every organization and individual.

### Key concepts
- **CIA triad:** confidentiality, integrity and availability.
- **Attackers:** criminals, insiders, activists and nation-states.
- **Defence in depth:** several layers of protection.
- **Risk:** the likelihood of an event combined with its impact.

### Definitions
- **Threat:** something that could cause harm.
- **Vulnerability:** a weakness that could be exploited.
- **Exploit:** a method of taking advantage of a vulnerability.
- **Malware:** software designed to cause harm.

### Example
A hospital's patient records need all three CIA goals: only staff may read them (confidentiality), they must not be altered (integrity), and doctors must reach them in an emergency (availability).

### Key takeaways
- Security is about reducing risk; nothing is perfectly secure.
- People are often the weakest link, so training matters.
- Layered defences mean one failure doesn't lose everything.

## module: Networking Fundamentals for Security
video: NIRXtMg-0z8

### Introduction
Most attacks travel over networks. Knowing how data moves is the foundation for understanding how to attack and protect it.

### Key concepts
- **TCP/IP model:** application, transport, internet and link layers.
- **IP addresses and ports:** identify a machine and a service.
- **Common protocols:** HTTP/HTTPS, DNS, SSH.
- **Network devices:** routers, switches and firewalls.
- **Segmentation:** splitting a network to contain breaches.

### Definitions
- **Packet:** a small unit of data sent across a network.
- **Port:** a numbered endpoint for a service, such as 443 for HTTPS.
- **Firewall:** filters traffic according to rules.
- **DNS:** translates domain names into IP addresses.

### Example
Visiting a website: DNS finds the server's IP address, TCP opens a connection to port 443, TLS encrypts the session, and HTTP requests the page. An attacker could target any of these steps.

### Key takeaways
- Close ports and services that aren't needed.
- Prefer encrypted protocols such as HTTPS and SSH.
- Monitoring traffic helps reveal suspicious activity.

## module: Common Cyber Threats and Vulnerabilities
video: awhqnSskWjU

### Introduction
Most attacks use a small set of well-known techniques. Recognizing them is the first step in defending against them.

### Key concepts
- **Phishing:** fake messages that trick people into giving up information.
- **Ransomware:** encrypts files and demands payment.
- **DoS and DDoS:** overwhelm a service with traffic.
- **Man-in-the-middle:** intercepts communication.
- **Weak configuration:** default passwords and unpatched software.

### Definitions
- **Social engineering:** manipulating people rather than technology.
- **Zero-day:** a flaw that has no fix yet.
- **Patch:** an update that fixes a vulnerability.
- **Botnet:** a network of compromised devices.

### Example
An email claims to be from your bank and links to a look-alike site. If you enter your password there, the attacker captures it. Warning signs include urgency and a slightly wrong sender address.

### Key takeaways
- Keep software updated, since many attacks use known flaws.
- Verify unexpected requests through another channel.
- Keep tested offline backups to recover from ransomware.

## module: Cryptography and Encryption
video: kaB7Vfj0DKs

### Introduction
Cryptography protects information even when others can see it. It underlies HTTPS, secure messaging and password storage.

### Key concepts
- **Symmetric encryption:** one shared key, as in AES; fast.
- **Asymmetric encryption:** a public and private key pair, as in RSA.
- **Hashing:** one-way fingerprints, such as SHA-256.
- **Digital signatures:** prove who sent something and that it wasn't changed.

### Definitions
- **Plaintext:** readable data.
- **Ciphertext:** encrypted data.
- **Key:** the secret value controlling encryption.
- **Salt:** random data added before hashing a password.

### Example
HTTPS combines both types: asymmetric cryptography lets the browser and server agree on a key safely, and then symmetric encryption protects the rest of the session because it's much faster.

### Key takeaways
- Encryption can be reversed with a key; hashing cannot be reversed.
- Store passwords using a slow salted hash such as bcrypt.
- Never invent your own cryptography; use proven libraries.

## module: Authentication and Access Control
video: 5uNifnVlBy4

### Introduction
Authentication proves who someone is, and authorization decides what they may do. Together they control who reaches a system's data.

### Key concepts
- **Authentication factors:** something you know, have or are.
- **Multi-factor authentication (MFA):** combines two or more factors.
- **Access control models:** role-based (RBAC) and attribute-based.
- **Least privilege:** give only the access that is needed.
- **Single sign-on:** one login for many services.

### Definitions
- **Authentication:** verifying identity.
- **Authorization:** granting permissions.
- **IAM:** Identity and Access Management.
- **Session token:** proof of a successful login.

### Example
In a learning platform, a student may view enrolled courses, an instructor may edit their own courses, and an admin may manage everything. A `401` means "not logged in"; a `403` means "not allowed".

### Key takeaways
- MFA stops most attacks that use stolen passwords.
- Review and remove unused access regularly.
- Enforce permissions on the server, not only in the user interface.

## module: Network and Web Application Security
video: -7OX58nHPb8

### Introduction
Web applications are exposed to the whole internet, which makes them frequent targets. The OWASP Top 10 lists the most critical web security risks.

### Key concepts
- **SQL injection:** user input runs as a database command.
- **Cross-site scripting (XSS):** attacker scripts run in other users' browsers.
- **Broken access control:** users reach data they shouldn't.
- **CSRF:** tricks a logged-in browser into making requests.
- **Network defences:** firewalls, VPNs and intrusion detection.

### Definitions
- **OWASP:** a non-profit improving software security.
- **Input validation:** rejecting unexpected data.
- **Output encoding:** making data safe to display.
- **VPN:** an encrypted tunnel across a network.

### Example
```sql
-- Dangerous if built by joining strings, because input can become SQL:
SELECT * FROM users WHERE email = '' OR '1'='1';
-- A parameterized query treats input only as data:
SELECT * FROM users WHERE email = ?;
```

### Key takeaways
- Never trust user input.
- Use parameterized queries and HTTPS everywhere.
- Check authorization on every request.

## module: Ethical Hacking Fundamentals and Security Testing
video: LxPZmkyT9fc

### Introduction
Ethical hackers use attackers' methods, with permission, to find weaknesses before criminals do. Written authorization is what separates ethical testing from a crime.

### Key concepts
- **Penetration testing phases:** planning, reconnaissance, scanning, exploitation, reporting.
- **Vulnerability scanning:** automated checks for known flaws.
- **Rules of engagement:** an agreed scope and limits.
- **Remediation:** the goal is fixing problems, not just finding them.

### Definitions
- **Penetration test:** an authorized simulated attack.
- **Reconnaissance:** gathering information about a target.
- **White-box testing:** the tester has full knowledge of the system.
- **Black-box testing:** the tester has no prior knowledge.

### Example
A company hires a tester to examine its web store. They agree on what is in scope in writing, the tester finds an insecure login form, and the report explains the risk and how to fix it.

### Key takeaways
- Never test systems you don't have written permission to test.
- Practise legally on deliberately vulnerable labs.
- A clear report is as important as the technical work.

## module: Security Best Practices and Incident Response
video: IRSQEO0koYY

### Introduction
Good habits prevent most incidents, but some will still happen. A prepared response limits the damage and speeds up recovery.

### Key concepts
- **Everyday practice:** patching, MFA, strong passwords, backups and training.
- **NIST incident response lifecycle:** preparation; detection and analysis; containment, eradication and recovery; post-incident activity.
- **Logging and monitoring:** without logs, attacks go unseen.
- **Lessons learned:** improve defences after every incident.

### Definitions
- **Incident:** an event that threatens security.
- **Containment:** stopping an attack from spreading.
- **SIEM:** a tool that collects and analyses security logs.
- **Backup:** a separate copy of data for recovery.

### Example
Ransomware hits one laptop. The team disconnects it (containment), removes the malware (eradication), restores files from backup (recovery), then reviews how it got in and adds email filtering (lessons learned).

### Key takeaways
- Write and rehearse a response plan before you need it.
- Test that backups actually restore.
- Report suspicious events quickly; speed limits damage.
