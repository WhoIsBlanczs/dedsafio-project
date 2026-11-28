# Traduce nombres intermediary (1.21.1) a nombres de Mojang, solo para los tokens listados.
import sys,re
toks=set(open('herramientas_tokens.txt').read().split())
# mojang: named -> official
cls_n2o={}; cls_o2n={}; mem={}  # (official_class, official_name, kind) -> named
cur=None
for f in ['out/client-1.21.1.txt','out/server-1.21.1.txt']:
    for line in open(f):
        if line.startswith('#'): continue
        if not line.startswith(' '):
            n,o=line.strip().rstrip(':').split(' -> '); cur=o; cls_o2n[o]=n.replace('.','/')
        else:
            l=line.strip(); left,o=l.split(' -> ')
            left=re.sub(r'^\d+:\d+:','',left)
            typ,rest=left.split(' ',1)
            if '(' in rest: name=rest[:rest.index('(')]; mem[(cur,o,'m')]=name
            else: mem[(cur,o,'f')]=rest
out=[]
ns=None; cc=None
for line in open('out/intermediary-1.21.1.tiny'):
    p=line.rstrip('\n').split('\t')
    if p[0]=='tiny': continue
    if p[0]=='c':
        o,i=p[1],p[2]; cc=o.replace('/','.')
        short=i.split('/')[-1].split('$')[-1]
        if short in toks: out.append(f'C\t{i}\t{cls_o2n.get(cc,i)}')
    elif len(p)>1 and p[1] in ('m','f'):
        kind=p[1]; o=p[3]; i=p[4]
        if i in toks:
            n=mem.get((cc,o,kind))
            if n: out.append(f'{kind.upper()}\t{i}\t{n}')
for l in sorted(set(out)): print(l)
