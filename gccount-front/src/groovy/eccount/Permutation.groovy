def makePermutations = { l -> l.permutations() }

def list = ['Crosby', 'Prayag', 'Nash', 'Young']
def permutations = makePermutations(list)
assert permutations.size() == (1..<(list.size()+1)).inject(1) { prod, i -> prod*i }
permutations.each { println it }
