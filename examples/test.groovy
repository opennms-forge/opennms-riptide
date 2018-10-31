
def flow = riptide.flow()
        .withRecord(riptide.record()
            .withSrcAddr(8,8,8,8))
        .withRecord(riptide.record()
            .withSrcAddr(8,8,8,8))
        .withRecord(riptide.record()
            .withSrcAddr(8,8,8,8))

riptide.send(flow)
