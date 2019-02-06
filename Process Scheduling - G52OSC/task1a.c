#include <stdio.h>
#include <stdlib.h>
#include "coursework.h"

int totalResp = 0;
int totalTurn = 0;
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

struct process * removeFromList(struct process * head){
	if(head == NULL){
		return NULL;
	} else {
		struct process * temp = head->oNext;
		free(head);
		return temp;
	}
}

void main(){
	struct process *newProcess = NULL;
	
	int i = 0;
	
	for(i = 0; i < NUMBER_OF_PROCESSES; i++){
		newProcess = generateProcess();
		head = addToList(head, newProcess);
	} 
	
	struct timeval start;
	struct timeval end;
	struct process *check = head;
	while(check != NULL){
		int resp = 0;
		int turn = 0;
		long int initial_burst_time = check->iBurstTime;
		simulateSJFProcess(check, &start, &end);
		turn = getDifferenceInMilliSeconds(check->oTimeCreated, end);
		resp = getDifferenceInMilliSeconds(check->oTimeCreated, start);
		totalResp += resp;
		totalTurn += turn;
		printf("Process ID = %d, Previous Burst Time = %ld, New Burst Time = %d , Response Time = %d, Turn Around Time = %d\n", 
			check->iProcessId, 
			initial_burst_time, 
			check->iBurstTime, 
			resp, 
			turn
		);
		
		if(head->iState == FINISHED){
			check = removeFromList(check);
		} 

	}
	printf("The average Response Time is: %f\n", (double)totalResp / NUMBER_OF_PROCESSES);
	printf("The average Turn Around Time is: %f\n", (double)totalTurn / NUMBER_OF_PROCESSES);	
}