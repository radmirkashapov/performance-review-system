"use strict";(self.webpackChunkprs_ui=self.webpackChunkprs_ui||[]).push([[261],{9261:(Y,v,r)=>{r.r(v),r.d(v,{TestPerformanceReviewModule:()=>D});var m=r(4755),d=r(4660),a=r(9401),A=r(4707),f=r(2722),g=r(262),_=r(2843),E=r(9646),l=(()=>((l=l||{})[l.SINGLE_ANSWER=0]="SINGLE_ANSWER",l[l.MULTIPLE_CHOICE=1]="MULTIPLE_CHOICE",l))(),c=(()=>((c=c||{})[c.ANSWER_BY_QUESTION=0]="ANSWER_BY_QUESTION",c[c.ANSWER_BY_PROBLEM=1]="ANSWER_BY_PROBLEM",c))(),e=r(2223),R=r(4452),M=r(8548);let B=(()=>{class n{constructor(t){this.glbApiService=t}doAnswer(t,o){return this.glbApiService.post(`/api/v1/performance-review/${t}/do-answer`,o)}}return n.\u0275fac=function(t){return new(t||n)(e.LFG(M.z))},n.\u0275prov=e.Yz7({token:n,factory:n.\u0275fac,providedIn:"root"}),n})();var p=r(6012),w=r(8097),C=r(9114),T=r(1728),P=r(5145),O=r(1292),h=r(4867);function F(n,u){1&n&&e._UZ(0,"app-spinner-wrapper")}function S(n,u){1&n&&(e.TgZ(0,"h3",2),e._uU(1,"\u041f\u0440\u043e\u0438\u0437\u043e\u0448\u043b\u0430 \u043e\u0448\u0438\u0431\u043a\u0430. \u041f\u043e\u043f\u0440\u043e\u0431\u0443\u0439\u0442\u0435 \u043f\u043e\u0434\u043e\u0436\u0434\u0430\u0442\u044c 30 \u0441\u0435\u043a\u0443\u043d\u0434 \u0438 \u043f\u043e\u0432\u0442\u043e\u0440\u0438\u0442\u044c \u043e\u0442\u0432\u0435\u0442. \u0415\u0441\u043b\u0438 \u043e\u0448\u0438\u0431\u043a\u0430 \u043f\u043e\u0432\u0442\u043e\u0440\u0438\u0442\u0441\u044f, \u043e\u0431\u043d\u043e\u0432\u0438\u0442\u0435 \u0441\u0442\u0440\u0430\u043d\u0438\u0446\u0443"),e.qZA())}function I(n,u){if(1&n&&(e.TgZ(0,"mat-radio-button",13),e._uU(1),e.qZA()),2&n){const t=u.$implicit;e.Q6J("value",t),e.xp6(1),e.hij(" ",t," ")}}function Q(n,u){if(1&n){const t=e.EpF();e.TgZ(0,"section",8)(1,"label"),e._uU(2),e.qZA(),e.TgZ(3,"mat-radio-group",9),e.NdJ("ngModelChange",function(i){e.CHM(t);const s=e.oxw(4);return e.KtG(s.radioButtonOption=i)}),e.YNc(4,I,2,2,"mat-radio-button",10),e.qZA(),e.TgZ(5,"mat-form-field",11)(6,"mat-label"),e._uU(7,"\u041b\u0438\u0431\u043e \u0443\u043a\u0430\u0436\u0438\u0442\u0435 \u0441\u0441\u044b\u043b\u043a\u0443 \u043d\u0430 \u0437\u0430\u0434\u0430\u0447\u0443, \u0432 \u043a\u043e\u0442\u043e\u0440\u043e\u0439 \u043f\u0440\u0438\u043c\u0435\u043d\u044f\u043b\u0430\u0441\u044c \u0434\u0430\u043d\u043d\u0430\u044f \u043a\u043e\u043c\u043f\u0435\u0442\u0435\u043d\u0446\u0438\u044f"),e.qZA(),e.TgZ(8,"input",12),e.NdJ("ngModelChange",function(i){e.CHM(t);const s=e.oxw(4);return e.KtG(s.taskUrl=i)}),e.qZA()()()}if(2&n){const t=e.oxw().ngIf,o=e.oxw(3);e.xp6(2),e.Oqu(t.nextQuestion),e.xp6(1),e.Q6J("ngModel",o.radioButtonOption),e.xp6(1),e.Q6J("ngForOf",t.nextQuestionAnswerOptions),e.xp6(4),e.Q6J("ngModel",o.taskUrl)}}function N(n,u){if(1&n&&(e.TgZ(0,"mat-checkbox",16),e._uU(1),e.qZA()),2&n){const t=u.$implicit;e.Q6J("formControlName",t),e.xp6(1),e.Oqu(t)}}function y(n,u){if(1&n){const t=e.EpF();e.TgZ(0,"section")(1,"label"),e._uU(2),e.qZA(),e.TgZ(3,"section",14),e.YNc(4,N,2,2,"mat-checkbox",15),e.qZA(),e.TgZ(5,"mat-form-field",11)(6,"mat-label"),e._uU(7,"\u041b\u0438\u0431\u043e \u0443\u043a\u0430\u0436\u0438\u0442\u0435 \u0441\u0441\u044b\u043b\u043a\u0443 \u043d\u0430 \u0437\u0430\u0434\u0430\u0447\u0443, \u0432 \u043a\u043e\u0442\u043e\u0440\u043e\u0439 \u043f\u0440\u0438\u043c\u0435\u043d\u044f\u043b\u0430\u0441\u044c \u0434\u0430\u043d\u043d\u0430\u044f \u043a\u043e\u043c\u043f\u0435\u0442\u0435\u043d\u0446\u0438\u044f"),e.qZA(),e.TgZ(8,"input",12),e.NdJ("ngModelChange",function(i){e.CHM(t);const s=e.oxw(4);return e.KtG(s.taskUrl=i)}),e.qZA()()()}if(2&n){const t=e.oxw().ngIf,o=e.oxw(3);e.xp6(2),e.Oqu(t.nextQuestion),e.xp6(1),e.Q6J("formGroup",o.checkBoxAnswerOptionsFormGroup),e.xp6(1),e.Q6J("ngForOf",t.nextQuestionAnswerOptions),e.xp6(4),e.Q6J("ngModel",o.taskUrl)}}function b(n,u){if(1&n){const t=e.EpF();e.TgZ(0,"mat-card")(1,"div")(2,"mat-card-header"),e._uU(3),e.qZA()(),e.TgZ(4,"div")(5,"mat-card-subtitle"),e._uU(6),e.qZA()(),e.TgZ(7,"div")(8,"div")(9,"mat-card-content"),e.YNc(10,Q,9,4,"section",5),e.YNc(11,y,9,4,"section",0),e.qZA()(),e.TgZ(12,"div")(13,"mat-card-actions")(14,"button",6),e.NdJ("click",function(){const s=e.CHM(t).ngIf,x=e.oxw(3);return e.KtG(x.doAnswer(s.nextQuestionId,s.nextSkill))}),e._uU(15," \u041e\u0442\u0432\u0435\u0442\u0438\u0442\u044c "),e.qZA(),e.TgZ(16,"button",7),e.NdJ("click",function(){e.CHM(t);const i=e.oxw(3);return e.KtG(i.cleanSelected())}),e._uU(17,"\u041e\u0447\u0438\u0441\u0442\u0438\u0442\u044c"),e.qZA()()()()()}if(2&n){const t=e.oxw(3);e.xp6(3),e.Oqu(t.test.name),e.xp6(3),e.Oqu(t.test.description),e.xp6(4),e.Q6J("ngIf",!t.isMultipleSelection),e.xp6(1),e.Q6J("ngIf",t.isMultipleSelection),e.xp6(3),e.Q6J("disabled",t.doAnswerButtonDisabled)}}function U(n,u){if(1&n&&(e.ynx(0,4),e.YNc(1,b,18,5,"mat-card",0),e.ALo(2,"async"),e.BQk()),2&n){const t=e.oxw(2);e.xp6(1),e.Q6J("ngIf",e.lcZ(2,1,t.question$))}}function Z(n,u){if(1&n&&(e.ynx(0),e.YNc(1,U,3,3,"ng-container",3),e.BQk()),2&n){const t=e.oxw();e.xp6(1),e.Q6J("ngIf",t.test)}}const k=[{path:"",component:(()=>{class n{constructor(t,o,i,s,x){this.formBuilder=t,this.testService=o,this.performanceReviewService=i,this.router=s,this.route=x,this.isLoading=!0,this.hasError=!1,this.radioButtonOption=void 0,this.taskUrl=void 0,this.isMultipleSelection=!1,this.checkBoxAnswerOptionsFormGroup=new a.cw({}),this.destroy$=new A.t(1),this.doAnswerButtonDisabled=!!this.taskUrl||!!this.radioButtonOption}ngOnInit(){this.isLoading=!0,this.route.params.pipe((0,f.R)(this.destroy$)).subscribe(t=>{this.testId=t.testId,this.sessionId=t.sessionId,this.testService.getById(this.testId).pipe((0,g.K)(o=>(this.hasError=!0,this.isLoading=!1,(0,_._)(o)))).subscribe(o=>{this.test=o,this.performanceReviewService.doAnswer(this.testId).pipe((0,f.R)(this.destroy$),(0,g.K)(i=>(this.hasError=!0,this.isLoading=!1,(0,_._)(i)))).subscribe(i=>{this.processNextQuestion(i)})})})}cleanSelected(){this.taskUrl=void 0,this.isMultipleSelection?this.checkBoxAnswerOptionsFormGroup.reset():this.radioButtonOption=void 0}doAnswer(t,o){this.isLoading=!0;const i={skill:o,questionId:t,answer:{}};console.log("isMultipleSelection",this.isMultipleSelection,this.taskUrl,this.radioButtonOption,this.checkBoxAnswerOptionsFormGroup.getRawValue()),this.taskUrl&&(i.answer={[c[c.ANSWER_BY_PROBLEM]]:[this.taskUrl]}),!this.isMultipleSelection&&this.radioButtonOption&&(i.answer={...i.answer,[c[c.ANSWER_BY_QUESTION]]:[this.radioButtonOption]}),this.isMultipleSelection&&this.checkBoxAnswerOptionsFormGroup&&(console.log("this.isMultipleSelection && this.checkBoxAnswerOptionsFormGroup",this.checkBoxAnswerOptionsFormGroup.value),i.answer={...i.answer,[c[c.ANSWER_BY_QUESTION]]:Object.entries(this.checkBoxAnswerOptionsFormGroup.value).filter(s=>s[1]).map(s=>s[0])}),console.log("answerBody",i),this.performanceReviewService.doAnswer(this.testId,i).pipe((0,f.R)(this.destroy$),(0,g.K)(s=>(this.hasError=!0,this.isLoading=!1,(0,_._)(s)))).subscribe(s=>{this.processNextQuestion(s)})}processNextQuestion(t){if(console.log("processNextQuestion",t),this.instanceOfPerformanceReviewDoAnswerResponse(t)){this.clean();const o=t;return console.log("nextQuestion",o),this.question$=(0,E.of)(o),o.nextQuestionType.toString()===l[l.MULTIPLE_CHOICE]?(this.buildCheckboxAnswerOptionsFormGroup(o.nextQuestionAnswerOptions),this.isMultipleSelection=!0):this.isMultipleSelection=!1,void(this.isLoading=!1)}if(this.instanceOfPerformanceReviewDoAnswerFinishTestResponse(t))return console.log("TEST FINISHED"),this.isLoading=!1,void this.router.navigateByUrl("/profile/me")}buildCheckboxAnswerOptionsFormGroup(t){const o={};this.checkBoxAnswerOptionsFormGroup.reset(),t.forEach(i=>{o[i]=new a.NI(!1)}),this.checkBoxAnswerOptionsFormGroup=this.formBuilder.group(o)}ngOnDestroy(){this.destroy$.next(!0),this.destroy$.complete()}instanceOfPerformanceReviewDoAnswerResponse(t){return"nextQuestionId"in t}instanceOfPerformanceReviewDoAnswerFinishTestResponse(t){return"nextQuestionDifficulty"in t}clean(){this.radioButtonOption=void 0,this.taskUrl=void 0,this.checkBoxAnswerOptionsFormGroup.reset()}}return n.\u0275fac=function(t){return new(t||n)(e.Y36(a.qu),e.Y36(R.q),e.Y36(B),e.Y36(d.F0),e.Y36(d.gz))},n.\u0275cmp=e.Xpm({type:n,selectors:[["app-test-performance-review"]],decls:3,vars:3,consts:[[4,"ngIf"],["style","color: red",4,"ngIf"],[2,"color","red"],["style","height: 100%",4,"ngIf"],[2,"height","100%"],["class","radio-section",4,"ngIf"],["mat-raised-button","","type","submit","color","primary",3,"disabled","click"],["mat-raised-button","","color","warn",3,"click"],[1,"radio-section"],[3,"ngModel","ngModelChange"],[3,"value",4,"ngFor","ngForOf"],[2,"width","100%"],["matInput","","placeholder","\u0421\u0441\u044b\u043b\u043a\u0430 \u043d\u0430 \u0437\u0430\u0434\u0430\u0447\u0443",3,"ngModel","ngModelChange"],[3,"value"],[1,"checkbox-group",3,"formGroup"],[3,"formControlName",4,"ngFor","ngForOf"],[3,"formControlName"]],template:function(t,o){1&t&&(e.YNc(0,F,1,0,"app-spinner-wrapper",0),e.YNc(1,S,2,0,"h3",1),e.YNc(2,Z,2,1,"ng-container",0)),2&t&&(e.Q6J("ngIf",o.isLoading),e.xp6(1),e.Q6J("ngIf",o.hasError),e.xp6(1),e.Q6J("ngIf",!o.isLoading))},dependencies:[m.sg,m.O5,p.a8,p.hq,p.dn,p.dk,p.$j,w.Nt,C.KE,C.hX,T.lW,a.Fj,a.JJ,a.JL,a.On,a.sg,a.u,P.C,O.oG,h.VQ,h.U0,m.Ov],styles:["mat-card[_ngcontent-%COMP%]   mat-card-subtitle[_ngcontent-%COMP%]{margin-left:15px}mat-card[_ngcontent-%COMP%]   label[_ngcontent-%COMP%]{font-size:20px;margin-bottom:8px}mat-card[_ngcontent-%COMP%]   mat-radio-group[_ngcontent-%COMP%]{display:flex;flex-direction:column;margin-top:30px}mat-card[_ngcontent-%COMP%]   mat-radio-group[_ngcontent-%COMP%]   mat-radio-button[_ngcontent-%COMP%]{margin-bottom:8px}mat-card[_ngcontent-%COMP%]   .checkbox-group[_ngcontent-%COMP%]{display:flex;flex-direction:column;margin-top:30px}mat-card[_ngcontent-%COMP%]   .checkbox-group[_ngcontent-%COMP%]   mat-checkbox[_ngcontent-%COMP%]{margin-bottom:8px}mat-card[_ngcontent-%COMP%]   mat-card-actions[_ngcontent-%COMP%]{display:flex;justify-content:space-between;flex-direction:row}"]}),n})()}];let J=(()=>{class n{}return n.\u0275fac=function(t){return new(t||n)},n.\u0275mod=e.oAB({type:n}),n.\u0275inj=e.cJS({imports:[d.Bz.forChild(k),d.Bz]}),n})();var G=r(332),L=r(3939);let D=(()=>{class n{}return n.\u0275fac=function(t){return new(t||n)},n.\u0275mod=e.oAB({type:n}),n.\u0275inj=e.cJS({imports:[m.ez,J,G.T5,p.QW,w.c,T.ot,a.u5,a.UX,L.G,O.p9,h.Fk]}),n})()}}]);