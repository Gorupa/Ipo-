import express from "express"; import cors from "cors";
const app=express(); app.use(cors()); app.use(express.json());
const now=()=>new Date().toISOString();
const ipos=[
{id:"demo1",name:"Example Technologies IPO",symbol:"EXAMPLE",type:"Mainboard",status:"OPEN",priceLow:700,priceHigh:735,lotSize:20,gmp:85,gmpUpdatedAt:now(),subscription:{qib:2.14,nii:4.62,retail:1.38,employee:3.05,overall:2.91},issueSize:"₹2,450 Cr",dates:{open:"2026-09-18",close:"2026-09-22",allotment:"2026-09-23",refund:"2026-09-24",listing:"2026-09-25"},description:"Demo data for UI testing.",registrar:"Example Registrar"},
{id:"demo2",name:"Sample Consumer IPO",symbol:"SAMPLE",type:"Mainboard",status:"UPCOMING",priceLow:420,priceHigh:445,lotSize:33,gmp:52,gmpUpdatedAt:now(),subscription:{qib:0,nii:0,retail:0,employee:0,overall:0},issueSize:"₹1,180 Cr",dates:{open:"2026-09-24",close:"2026-09-28",allotment:"2026-09-29",refund:"2026-09-30",listing:"2026-10-01"},description:"Demo upcoming IPO data.",registrar:"Sample Registrar"}];
app.get("/health",(_,r)=>r.json({ok:true,service:"ipo-pulse-api"}));
app.get("/api/ipos",(q,r)=>{let s=q.query.status; r.json({updatedAt:now(),data:s?ipos.filter(x=>x.status===s.toUpperCase()):ipos})});
app.get("/api/ipos/:id",(q,r)=>{let x=ipos.find(x=>x.id===q.params.id); x?r.json({updatedAt:now(),data:x}):r.status(404).json({error:"IPO not found"})});
app.get("/api/ipos/:id/gmp-history",(q,r)=>{let x=ipos.find(x=>x.id===q.params.id); if(!x)return r.status(404).json({error:"IPO not found"}); r.json({data:[-25,-18,-12,-5,0].map((d,i)=>({value:x.gmp+d,date:new Date(Date.now()-(4-i)*86400000).toISOString()}))})});
app.listen(process.env.PORT||10000,()=>console.log("IPO Pulse API running"));
