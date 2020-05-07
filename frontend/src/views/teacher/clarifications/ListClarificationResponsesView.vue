<template>
  <div>
    <v-alert
      v-model="creationAlertSuccess"
      class="creationAlertSuccess"
      type="success"
      dismissible
      data-cy="SuccessMessage"
    >
      The additional clarification request was successfully created.
    </v-alert>
    <v-card class="table">
      <v-data-table
        :headers="getHeader()"
        :items="clarificationResponses"
        :search="search"
        disable-pagination
        :hide-default-footer="true"
        :mobile-breakpoint="0"
        multi-sort
      >
        <template v-slot:top>
          <v-card-title>
            <v-text-field
              v-model="search"
              append-icon="search"
              label="Search"
              class="mx-2"
            />
            <v-spacer />
            <v-dialog v-model="dialog" persistent max-width="400px" v-if="!isTeacher">
              <template v-slot:activator="{ on }">
                <v-btn v-on="on" data-cy="askForAdditionalClarificationButton">
                  Ask for additional clarification
                </v-btn>
              </template>
              <v-card>
                <v-card-title>
                  <span class="headline">Ask for additional clarification</span>
                </v-card-title>
                <v-card-text> </v-card-text>
                <v-card-actions>
                  <v-spacer></v-spacer>
                  <v-btn
                    color="blue darken-1"
                    text
                    @click="dialog = false"
                    data-cy="CancelButton"
                    >Cancel</v-btn
                  >
                  <v-btn
                    color="blue darken-1"
                    text
                    @click="askForAdditionalClarification"
                    data-cy="SubmitButton"
                  >
                    Confirm
                  </v-btn>
                  <v-spacer></v-spacer>
                </v-card-actions>
              </v-card>
            </v-dialog>
          </v-card-title>
        </template>

        <template v-slot:item.deleteClarificationResponse="{ item }">
          <v-tooltip bottom>
            <template v-slot:activator="{ on }">
              <v-icon
                class="mr-2"
                v-on="on"
                @click="deleteClarificationResponse(item.id)"
                color="#D32F2F"
                data-cy="DeleteClarificationResponseIcon"
                >delete</v-icon
              >
            </template>
            <span>Remove Response</span>
          </v-tooltip>
        </template>
      </v-data-table>
    </v-card>
  </div>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import StatementClarificationResponse from '@/models/statement/StatementClarificationResponse';
import RemoteServices from '@/services/RemoteServices';

@Component
export default class ListClarificationResponses extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop(String) readonly clarificationQuestionId!: string;
  clarificationResponses: StatementClarificationResponse[] = [];
  isTeacher: boolean = false;
  search: string = '';
  creationAlertSuccess: boolean = false;
  headersTeacher: object = [
    {
      text: 'Response Content',
      value: 'teacherResponse',
      align: 'center',
      width: '35%'
    },
    {
      text: 'Response Date',
      value: 'responseDate',
      align: 'center',
      width: '15%'
    },
    {
      text: 'Remove',
      value: 'deleteClarificationResponse',
      align: 'center',
      width: '5%'
    }
  ];
  headersStudent: object = [
    {
      text: 'Response Content',
      value: 'teacherResponse',
      align: 'center',
      width: '35%'
    },
    {
      text: 'Response Date',
      value: 'responseDate',
      align: 'center',
      width: '15%'
    }
  ];

  getHeader() {
    if (this.isTeacher) {
      return this.headersTeacher;
    } else return this.headersStudent;
  }

  async created() {
    await this.$store.dispatch('loading');
    try {
      if (this.$router.currentRoute.fullPath.includes('management')) {
        this.clarificationResponses = await RemoteServices.getTeacherClarificationResponse(
          parseInt(this.clarificationQuestionId)
        );
        this.isTeacher = true;
      } else
        this.clarificationResponses = await RemoteServices.getStudentClarificationResponse(
          parseInt(this.clarificationQuestionId)
        );
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async deleteClarificationResponse(clarificationResponseId: number) {
    if (
      confirm('Are you sure you want to delete this clarification response?')
    ) {
      try {
        await RemoteServices.deleteClarificationResponse(
          clarificationResponseId
        );
        this.clarificationResponses = this.clarificationResponses.filter(
          clarificationResponse =>
            clarificationResponse.id != clarificationResponseId
        );
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
      await this.$store.dispatch('clearLoading');
    }
  }

  async askForAdditionalClarification() {
    try {
      await RemoteServices.askForAdditionalClarification(
        parseInt(this.clarificationQuestionId)
      );
      this.creationAlertSuccess = true;
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');

    this.dialog = false;
  }
}
</script>

<style lang="scss" scoped>
.creationAlertSuccess {
  z-index: 9999;
  position: absolute;
  left: 20px;
  top: 80px;
  width: calc(100% - 40px);
}
</style>
