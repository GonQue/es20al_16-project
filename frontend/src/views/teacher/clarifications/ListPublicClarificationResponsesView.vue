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
        :headers="headersStudent"
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
          </v-card-title>
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

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.clarificationResponses = await RemoteServices.getStudentClarificationResponse(
        parseInt(this.clarificationQuestionId)
      );
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
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
