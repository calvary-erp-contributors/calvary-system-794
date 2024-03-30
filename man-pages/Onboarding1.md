# Calvary Systems Developer Onboarding Document ver 1

## Welcome to Calvary Systems!

Welcome to the team at Calvary Systems! We're excited to have you join us on our journey to develop automated solutions for Calvary and may be even churches in general. This document will guide you through the on-boarding process and help you get acquainted with our project and team.

### Table of Contents
1. Introduction to Calvary Systems
2. Role of an ERP System in a Church
3. Project Overview
4. Todoist Setup
5. Next Steps

## 1. Introduction to Calvary Systems

Calvary Systems is a software development project dedicated to creating tailored solutions for our church. Our goal is to empower the church with efficient tools to manage their operations and enhance their outreach efforts. By leveraging technology, we aim to streamline processes and enable better communication within church groups and departments, 
and also to provide near real time feedback to our members and congregants.

## 2. Role of an ERP System in a Church

An ERP (Enterprise Resource Planning) system plays a crucial role in the operations of a church. It serves as a centralized platform to manage various aspects including:

- Membership and Attendance Tracking
- Financial Management
- Event Planning and Scheduling
- Resource Allocation
- Communication and Outreach
- Reporting and Analytics

By implementing an ERP system, Calvary can optimize workflows, improve decision-making, and foster greater engagement among members.

## 3. Project Overview

### Project Name: Calvary ERP

### Objectives:
- Develop a comprehensive ERP system tailored for churches.
- Provide intuitive interfaces for easy navigation and usage.
- Ensure scalability and flexibility to accommodate churches of different sizes and needs. The idea is to have a tool that can collaborate between multiple institutions so that if one day 
  we end up having a number of Sabbath schools, or actual schools, hospitals, bookshops and whatever other businesses that the church might engage in, it will be possible to collate information 
  from various places and quickly collect group reporting information for a single reporting entity.
- Integrate communication tools to facilitate interaction among church members and staff. It will one day be important for us to have a single source of truth for deaconry, treasury departments and the
  Eldership, and other pseudo departments that a well organized church will require like event management, instrument calibration, car park management, security, sitting arrangements, scheduling, 
  camera crews, media team, children instructors, and so on and so forth. 
 
### Technologies Used:
Following is an unstructured list of technologies, and methodologies used
 - JHipster code generator (various versions starting from ver 7.9.4)
 - Monolith (As in microservices are great, but too resource-intensive and we also don't have immediate cloud deployment subscriptions so we are using what we have)
 - Security, user authentication and account - JWTs. Mind this is the JHipster implementation so the implementation is not an exact strict interpretation of the standard,
   but in general we do manage to have a client whose authentication is independent of the backend instance (stateless) and the backend needs to check authentication on each and every request
 - Websockets: We use the default (spring-websocket) implementation from JHipster code generator enables the backend to securely track every page visited by the user on the frontend. No further development on the base
   sample implementation, but we think could be used to track activity and review intuitiveness of the system for instance by checking the amount of time spent by a user on each of the pages
 - Java with Spring framework: The backend of the current implementation (Provenance Series) is Java version 11+ using Spring for IOC. This is the key technology proposition by JHipster whose
   ubiquity in data access, security and sheer availability of logging, testing tools, cache and persistence tools, and transparency of error handling is a major selling point.
 - ReactJS Front end: The front-end is implemented severally from the backend connecting to the backend through JWT-secured API. Current implementation consists ReactJS with Redux. ReactJS with Redux is the default 
   pattern implemented through the JHipster generator, but we also have bits of custom code that connect to backend directly through axios library. The React framework and ecosystem of libraries one of the tools around which you 
   have many communities and number of users and developers and was picked simply for the possibility of getting participation from many volunteers. 
 - [Experimental] Angular framework - We also have an ongoing experimental barely developed client created in Angular framework.
 - Kafka message broker for transaction account maintenance
 - Elasticsearch engine - full text search connecting to the backend through spring data library
 - Database: postgresql
 - Liquibase for database migration
 - Distributed cache provider: hazelcast
 - Client package manager: npm
 - Server package manager: maven (as opposed to gradle)

### Meet the team:
- June Gatacha
- Sam Wambua
- Bernie
- Daniel Njue
- Jimmy Mugane
- Mapenzi
- Edwin Njeru

## 4. Todoist Setup

To streamline our task management process, we use Todoist, a versatile app for managing to-do lists. Follow the steps below to join our project Todoist page:

1. Go to [Todoist](https://todoist.com) and sign up for an account if you haven't already.
2. Share your email address with Edwin Njeru to be added to the project Todoist page.
3. Once added, you'll have access to the project's tasks and can collaborate with other team members efficiently.

Current project tasks are listed into 3 sub-categories: 
 - Client tasks
 - Server tasks
 - Documentation tasks

## 5. Next Steps

- Complete Todoist setup and familiarize yourself with the project tasks.
- Review any existing documentation or resources provided.
- Start familiarizing yourself with the technologies and tools used in the project.

Welcome aboard, and we look forward to working together to make a positive impact on church communities!

If you have any questions or need further assistance, feel free to reach out to us on the whatsapp page. Please note this is an on-going documentation
subject to future improvements and amendments in accordance with the guidance of the developer team.

Best Regards,

Edwin Njeru

Calvary Systems

```Insights at the speed of the message```
