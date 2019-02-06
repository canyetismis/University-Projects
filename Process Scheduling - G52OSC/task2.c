
#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include "coursework.h"
#include <semaphore.h>

int totalResp = 0;
int totalTurn = 0;

pthread_mutex_t mutex;  // Controls access to critical section
sem_t empty;    		// counts number of empty buffer slots
sem_t full;             // counts number of full buffer slots

struct process *head = NULL;

struct process * addToList(struct process * head, struct process * new){
	struct process * curr = NULL;
	if(head == NULL || head->iBurstTime >= new->iBurstTime){
		new->oNext = head;
		head = new;
		return head;
	}
	else{
		curr = head;
		while(curr->oNext != NULL && curr->oNext->iBurstTime < new->iBurstTime){
			curr = curr->oNext;			
		}
		new->oNext = curr->oNext;
		curr->oNext = new;
		return head;
	}
}

struct process *printList(struct process *p){
	struct process *temp = p;
	while(temp->oNext != NULL){
		printf("-----Process number %d here with burst time %d\n", temp->iProcessId, temp->iBurstTime);
		temp = temp->oNext;
	}
	return p;
}

void funcProducer(){
	int i = 0;
	
	
	for(i = 0; i < NUMBER_OF_PROCESSES; i++){                  
		struct process *newProcess = NULL;
		newProcess = generateProcess();
		sem_wait(&empty);
		pthread_mutex_lock(&mutex);
		//printf("produce\n");
		head = addToList(head, newProcess);
		//printList(head);
		pthread_mutex_unlock(&mutex);
		sem_post(&full);
	
	}
	


}

void funcConsumer(){
	struct timeval start;
	struct timeval end;
	int i = 0;

	while(i < NUMBER_OF_PROCESSES){
		i++;
		sem_wait(&full); 
		pthread_mutex_lock(&mutex);

		struct process * temp = head;
		head = head->oNext;

		pthread_mutex_unlock(&mutex);
		sem_post(&empty);	

		int resp = 0;
		int turn = 0;
		long int initial_burst_time = temp->iBurstTime;
		simulateSJFProcess(temp, &start, &end);
		turn = getDifferenceInMilliSeconds(temp->oTimeCreated, end);
		resp = getDifferenceInMilliSeconds(temp->oTimeCreated, start);
		totalResp += resp;
		totalTurn += turn;
		
		printf("Process ID = %d, Previous Burst Time = %ld, New Burst Time = %d , Response Time = %d, Turn Around Time = %d\n", 
			temp->iProcessId, 
			initial_burst_time, 
			temp->iBurstTime, 
			resp, 
			turn);
		free(temp);
		
	}
	printf("The average Response Time is: %f\n", (double)totalResp / NUMBER_OF_PROCESSES);
	printf("The average Turn Around Time is: %f\n", (double)totalTurn / NUMBER_OF_PROCESSES);

    
}

int main(){
	pthread_t producer;
	pthread_t consumer;
	
	pthread_mutex_init(&mutex, NULL);
	sem_init(&empty,0,BUFFER_SIZE);
	sem_init(&full,0,0);
	
	pthread_create(&producer, NULL, (void*)funcProducer, NULL);
	pthread_create(&consumer, NULL, (void*)funcConsumer, NULL);

    pthread_join(producer, NULL);
    pthread_join(consumer, NULL);
}